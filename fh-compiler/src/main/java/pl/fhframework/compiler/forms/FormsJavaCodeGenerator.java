package pl.fhframework.compiler.forms;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.compiler.core.generator.*;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.forms.iterators.*;
import pl.fhframework.core.generator.*;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.forms.ICompilerAwareComponent;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.attribute.*;
import pl.fhframework.tools.loading.FormReader;

import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static pl.fhframework.core.util.StringUtils.firstLetterToUpper;
import static pl.fhframework.model.forms.AvailabilityConfiguration.Variant;

/**
 * Generator of forms. This is statefull, single usage implementation. This class is not thread-safe.
 */
public class FormsJavaCodeGenerator extends AbstractJavaClassCodeGenerator { // non-spring class - it is also used in non-spring context

    private ExpressionContext defaultExpressionContext;

    private Form<?> form;

    private Type modelClass;

    private GenerationContext xmlTimestampMethod;

    // TODO: remove after Tree is compiled
    private List<Component> notSupportedIteratorComponents = new ArrayList<>();

    private List<CompiledRepeatableIterator> currentIterators = new ArrayList<>();

    private Map<String, Field> componentInjectionDeclarations = new LinkedHashMap<>();

    private Stack<ExpressionContext> currentExpressionContexts = new Stack<>();

    private Set<ComponentWrapper> alreadyProcessedComponents = new LinkedHashSet<>();

    private DependenciesContext dependenciesContext;

    private Optional<Predicate<Component>> componentFilter;

    private InternalFormModelTypeProvider internalModelTypeProvider;

    private Set<ActionSignature> allActions = new LinkedHashSet<>();

    private RulesTypeDependecyProvider rulesTypeProvider;

    private EnumTypeDependencyProvider enumsTypeDependencyProvider;

    public <M, F extends Form<M>> FormsJavaCodeGenerator(Form<?> form, Type modelClass,
                                                         String targetClassPackage, String targetClassName, String baseClassName,
                                                         DependenciesContext dependenciesContext,
                                                         GenerationContext xmlTimestampMethod,
                                                         Optional<Predicate<Component>> componentFilter) {
        super(targetClassPackage, targetClassName, baseClassName);
        this.form = form;
        this.xmlTimestampMethod = xmlTimestampMethod;
        this.modelClass = modelClass;
        this.dependenciesContext = dependenciesContext;
        this.componentFilter = componentFilter;
        this.rulesTypeProvider = new RulesTypeDependecyProvider(dependenciesContext);
        this.enumsTypeDependencyProvider = new EnumTypeDependencyProvider(dependenciesContext);
        defaultExpressionContext = new ExpressionContext();
        defaultExpressionContext.setDefaultBindingRoot("getModel()", modelClass);
        defaultExpressionContext.addBindingRoot("THIS", "getModel()", modelClass);
        defaultExpressionContext.addBindingRoot("FORM", "getThisForm()", form.getClass());
        defaultExpressionContext.addBindingRoot(UserRoleTypeProvider.ROLE_PREFIX, "", UserRoleTypeProvider.ROLE_MARKER_TYPE);
        defaultExpressionContext.addBindingRoot(UserPermissionTypeProvider.PERM_PREFIX, "", UserPermissionTypeProvider.PERM_MARKER_TYPE);
        defaultExpressionContext.addBindingRoot(RulesTypeProvider.RULE_PREFIX, "__ruleService", DynamicRuleManager.RULE_HINT_TYPE);
        defaultExpressionContext.addBindingRoot(EnumsTypeProvider.ENUM_PREFIX, "", DynamicModelManager.ENUM_HINT_TYPE);
        for (LocaleBundle localeBundle : form.getLocaleBundle()) {
            defaultExpressionContext.addBindingRoot("$" + localeBundle.getVar(), "__getMessageService().getBundle(\"" + localeBundle.getBasename() + "\")", MessageService.MessageBundle.class);
        }
        defaultExpressionContext.addBindingRoot("$", "__getMessageService().getAllBundles()", MessageService.MessageBundle.class);
        currentExpressionContexts.push(defaultExpressionContext);
        if (modelClass == FormsManager.FORM_INTERNAL_MODEL_TYPE) {
            this.internalModelTypeProvider = new InternalFormModelTypeProvider(form.getModelDefinition(), dependenciesContext);
        }
    }

    protected void generateClassBody() {
        buildInjectedComponentsMap();
        addFixedFieldMapping("this", form);

        if (form instanceof AdHocForm) {
            if (modelClass == FormsManager.FORM_INTERNAL_MODEL_TYPE) {
                classSignatureSection.addLine("public class %s extends %s<%s.%s>", targetClassName, AdHocForm.class.getName(), targetClassName, FormsManager.FORM_INTERNAL_MODEL_CLASS_NAME);
            } else {
                classSignatureSection.addLine("public class %s extends %s<%s>", targetClassName, AdHocForm.class.getName(), toTypeLiteral(modelClass));
            }
        } else {
            classSignatureSection.addLine("public class %s extends %s", targetClassName, toTypeLiteral(form.getClass()));
        }

        constructorSignatureSection.addLine("public %s()", targetClassName);

        // add InternalModel class
        fieldSection.addSection(generateInternalModel(), 1);

        // add __conversionService autowired field
        fieldSection.addLine("@%s private %s __conversionService;",
                toTypeLiteral(Autowired.class), toTypeLiteral(FhConversionService.class));

        // add __getConversionService()
        methodSection.addLine("private %s %s() { return __conversionService; }",
                toTypeLiteral(FhConversionService.class), BindingJavaCodeGenerator.CONVERSION_SERVICE_GETTER);

        // Messages i18n
        fieldSection.addLine("@%s private %s __messagesService;",
                toTypeLiteral(Autowired.class), toTypeLiteral(MessageService.class));

        methodSection.addLine("private %s %s() { return __messagesService; }",
                toTypeLiteral(MessageService.class), BindingJavaCodeGenerator.MESSAGES_SERVICE_GETTER);

        // add __ruleService autowired field
        fieldSection.addLine("@%s private %s __ruleService;",
                toTypeLiteral(Autowired.class), toTypeLiteral(RulesService.class));

        // add getThis()
        methodSection.addLine("private %s getThisForm() { return this; }\n", targetClassName);

        // add static method returning XML timestamps
        methodSection.addSection(xmlTimestampMethod, 0);

        // process components
        processComponent(form, constructorSection, fieldSection, true);

        // add availability configuration
        new AvailabilityJavaCodeGenerator(this, form, constructorSection, fieldSection, methodSection).processAvailabilityConfiguration();

        // add signatures of form's actions
        addFormActionSignatures();

        // add variants of form's
        addFormVariants();

        // add form's static includes
        addFormStaticIncludes();

        validateNotInjectedComponents();
    }

    /*@Override
    protected boolean addGeneratedDynamicClass() {
        return true;
    }*/

    protected GenerationContext generateInternalModel() {
        GenerationContext modelClassSection = new GenerationContext();

        if (form instanceof AdHocForm && form.getModelDefinition() != null) {

            GenerationContext modelFieldSection = new GenerationContext();
            GenerationContext modelMethodSection = new GenerationContext();

            modelClassSection.addLine("@%s(%s)", toTypeLiteral(GeneratedDynamicClass.class),
                    toStringLiteral(targetClassPackage + "." + baseClassName + "$" + FormsManager.FORM_INTERNAL_MODEL_CLASS_NAME));
            modelClassSection.addLine("public static class %s {", FormsManager.FORM_INTERNAL_MODEL_CLASS_NAME);
            modelClassSection.addSection(modelFieldSection, 1);
            modelClassSection.addLine();
            modelClassSection.addSection(modelMethodSection, 1);
            modelClassSection.addLine("}");
            modelClassSection.addLine();

            for (Property property : form.getModelDefinition().getProperties()) {
                if (property.getType() == null || property.getName() == null) {
                    throw new FhFormException("Model element must define name and type");
                }

                Class<?> targetClass;
                boolean complexType = false;
                if (FormsManager.SUPPORTED_SIMPLE_FORM_INTERNAL_MODEL_CLASSES.contains(property.getType())) {
                    targetClass = ReflectionUtils.getClassForName(property.getType());
                } else {
                    DependencyResolution resolution = dependenciesContext.resolve(DynamicClassName.forClassName(property.getType()));
                    if (!resolution.isClassReady()) {
                        throw new FhFormException("Model class element type " + property.getType() + " is not ready yet");
                    }
                    targetClass = resolution.getReadyClass();
                    complexType = true;
                }
                Type targetType = targetClass;
                if (property.getMultiplicity() == Property.PropertyMultiplicity.MULTIPLE) {
                    targetType = ReflectionUtils.createCollectionType(List.class, ReflectionUtils.mapPrimitiveToWrapper(targetClass));
                    targetClass = List.class;
                } else if (property.getMultiplicity() == Property.PropertyMultiplicity.MULTIPLE_PAGEABLE) {
                    targetType = ReflectionUtils.createParametrizedType(PageModel.class, ReflectionUtils.mapPrimitiveToWrapper(targetClass));
                    targetClass = PageModel.class;
                }
                modelFieldSection.addLine();
                if (complexType) {
                    modelFieldSection.addLine("@%s", toTypeLiteral(Valid.class));
                }
                modelFieldSection.addLineWithIndent(1, "private %s %s;", toTypeLiteral(targetType), property.getName());

                String visibility = property.isPrivateProperty() ? "private" : "public";

                // getter
                modelMethodSection.addLine();
                modelMethodSection.addLine("@%s(type=%s.%s)", toTypeLiteral(ModelElement.class), toTypeLiteral(ModelElementType.class), ModelElementType.BUSINESS_PROPERTY.name());
                modelMethodSection.addLineWithIndent(1, "%s %s %s() { return %s; }", visibility, toTypeLiteral(targetType),
                        ReflectionUtils.getGetterName(property.getName(), targetClass), property.getName());

                // setter
                modelMethodSection.addLine();
                modelMethodSection.addLineWithIndent(1, "%s void set%s(%s %s) { this.%s = %s; }", visibility,
                        StringUtils.firstLetterToUpper(property.getName()), toTypeLiteral(targetType), property.getName(),
                        property.getName(), property.getName());
            }
        }

        return modelClassSection;
    }

    protected boolean supportsSpecializedLiteralExpressionCreation(Object object) {
        return object instanceof ModelBinding || object instanceof IndexedModelBinding || object instanceof ActionBinding;
    }

    protected String createSpecializedLiteralExpression(Object object, Component owner, Field field) {
        if (object instanceof AdHocModelBinding) {
            return getBindingGenerator().createBindingLiteral((AdHocModelBinding<?>) object, owner, field);
        } else if (object instanceof IndexedModelBinding) {
            return getBindingGenerator().createIndexedBindingLiteral((AdHocIndexedModelBinding<?>) object, owner, field);
        } else if (object instanceof AdHocActionBinding) {
            return getBindingGenerator().createActionBindingLiteral((AdHocActionBinding) object, owner, field, allActions);
        } else {
            throw new UnsupportedOperationException(); // as long as supportsSpecializedLiteralExpressionCreation() is OK this line is not reachable
        }
    }

    protected <T> String createLiteral(Component owner, T fieldValue, Field field) {
        Optional<IComponentAttributeTypeConverter> converterOptional = FormReader.getInstance().getAttributeConverter(owner.getClass(), field);
        String valueExpression;
        if (fieldValue == null) {
            return "null";
        }

        // converter has a priority
        if (converterOptional.isPresent()) {
            IComponentAttributeTypeConverter<T> converter = (IComponentAttributeTypeConverter<T>) converterOptional.get();
            GenerationContext initSection = new GenerationContext();
            String converted = toJavaLiteral(this, initSection, owner, fieldValue, converter);
            if (!initSection.isEmpty()) {
                constructorSection.addSection(initSection, 0);
            }
            return converted;
        }

        // simple types
        if (fieldValue instanceof String) {
            return toStringLiteral((String) fieldValue);
        }
        if (fieldValue instanceof Long) {
            return fieldValue.toString() + "L";
        }
        if (fieldValue instanceof Double) {
            return fieldValue.toString() + "d";
        }
        if (fieldValue instanceof Float) {
            return fieldValue.toString() + "f";
        }
        if (fieldValue instanceof Number || fieldValue instanceof Boolean) {
            return fieldValue.toString();
        }
        if (fieldValue instanceof Enum) {
            return fieldValue.getClass().getName().replace("$", ".") + "." + ((Enum) fieldValue).name();
        }
        if (supportsSpecializedLiteralExpressionCreation(fieldValue)) {
            return createSpecializedLiteralExpression(fieldValue, owner, field);
        }

        // last chance - string based constructor
        if (FormReader.getInstance().getStringBasedConstructorConverter(field.getType()).isPresent()) {
            // assume a type with string based constructor
            return String.format("new %s(%s)", field.getType().getName(), toStringLiteral(fieldValue.toString()));
        }

        throw new FhFormGeneratorException(getIdForException(owner), String.format("Unsupported type %s for literals generation for %s.%s. Please implement %s<%s>.",
                field.getType().getName(), owner.getClass().getName(), field.getName(),
                IComponentAttributeTypeConverter.class.getSimpleName(), field.getType().getSimpleName()));
    }

    private <T> String toJavaLiteral(FormsJavaCodeGenerator generator, GenerationContext initSection, Component owner, T value, IComponentAttributeTypeConverter<T> converter) {
        if (converter instanceof ClassByFullNameAttrConverter) {
            return generator.toTypeLiteral((Type) value) + ".class";
        }
        else if (converter instanceof CommaSeparatedStringListAttrConverter) {
            StringBuilder literal = new StringBuilder(Arrays.class.getName() + ".asList(");
            literal.append(((List<String>)value).stream()
                    .map(AbstractJavaCodeGenerator::toStringLiteral) // comvert to string literal
                    .collect(Collectors.joining(CommaSeparatedStringListAttrConverter.SEPARATOR))); // join with ,
            literal.append(')');
            return literal.toString();
        }
        else if (converter instanceof PresentationStyleAttrConverter) {
            return PresentationStyleEnum.class.getName().replace("$", ".") + "." + ((PresentationStyleEnum)value).name();
        }
        else if (converter instanceof ToLowerCaseEnumAttrConverter) {
            return ((ToLowerCaseEnumAttrConverter) converter).getEnumClass().getName().replace("$", ".") + "." + ((Enum)value).name();
        }
        else {
            Optional<String> literal = converter.toJavaLiteral(owner, value);
            if (literal.isPresent()) {
                return literal.get();
            }
        }

        throw new IllegalArgumentException("Unknown literal");
    }

    private void processComponent(Component component, GenerationContext initSection, GenerationContext declarationSection, boolean splitSection) {
        try {
            // avoid recursive processing aleady processed component
            ComponentWrapper componentWrapper = new ComponentWrapper(component);
            if (alreadyProcessedComponents.contains(componentWrapper)) {
                return;
            }

            alreadyProcessedComponents.add(componentWrapper);

            if (splitSection) {
                String componentName = resolveName(component);
                String initMethodName = "initCmp_" + componentName;
                boolean hasParent = component.getGroupingParentComponent() != null && !(component.getGroupingParentComponent() instanceof Form);
                if (hasParent) {
                    initSection.addLine("%s(%s, %s);", initMethodName, componentName, resolveName(component.getGroupingParentComponent()));
                }
                else if (!(component instanceof Form)){
                    initSection.addLine("%s(%s);", initMethodName, componentName);
                }
                else {
                    initSection.addLine("%s();", initMethodName);
                }

                initSection = new GenerationContext();
                if (hasParent) {
                    initSection.addLineWithIndent(0, "private void %s(%s %s, %s %s) {",
                            initMethodName, toTypeLiteral(component.getClass()), componentName,
                            toTypeLiteral(component.getGroupingParentComponent().getClass()), resolveName(component.getGroupingParentComponent()));
                }
                else if (!(component instanceof Form)){
                    initSection.addLineWithIndent(0, "private void %s(%s %s) {", initMethodName, toTypeLiteral(component.getClass()), componentName);
                }
                else {
                    initSection.addLineWithIndent(0, "private void %s() {", initMethodName);
                }
                methodSection.addSection(initSection, 0);
            }

            // TODO: remove
            boolean isNotSupportedIterable = isNotSupportedIterable(component);
            if (isNotSupportedIterable) {
                notSupportedIteratorComponents.add(component);
            }

            // inform compiler-aware components
            if (component instanceof ICompilerAwareComponent) {
                ((ICompilerAwareComponent) component).beforeCompilation();
            }

            // if within IIndexedBindingOwner, push iterators to context
            List<CompiledRepeatableIterator> optionalIndexedBindingIterators = null;
            if (component instanceof IIndexedBindingOwner) {
                optionalIndexedBindingIterators = pushAndGetIteratorsForIndexBinding((IIndexedBindingOwner) component);
            }

            Class targetClass = component.getClass();
            // @XMLProperty
            for (Field field : ReflectionUtils.getFieldsWithHierarchy(targetClass, XMLProperty.class)) {
                XMLProperty attrAnnotation = field.getAnnotation(XMLProperty.class);
                if (attrAnnotation.skipCompiler()) {
                    continue;
                }

                String attrName = attrAnnotation.value();
                if ("".equals(attrName)) {
                    attrName = field.getName();
                }

                Object fieldValue = getGetterValue(component, targetClass, field, XMLProperty.class);
                if (fieldValue == null) {
                    continue;
                }

                Class fieldType = field.getType();
                fieldType = ReflectionUtils.mapPrimitiveToWrapper(fieldType);

                String valueExpression = createLiteral(component, fieldValue, field);
                String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                initSection.addLine("%s.%s(%s);", resolveName(component), setterName, valueExpression);
            }

            // if within IIndexedBindingOwner, pop iterators from context
            if (optionalIndexedBindingIterators != null) {
                popIterators(optionalIndexedBindingIterators);
            }

            // subcomponents
            if (component instanceof IGroupingComponent) {
                IGroupingComponent<Component> castedGroup = (IGroupingComponent<Component>) component;
                processSubcomponents(component, castedGroup.getSubcomponents(), initSection, declarationSection, true, null,
                        splitSection && !IMultipleIteratorRepeatable.class.isInstance(component) && !(component.getGroupingParentComponent() instanceof IRepeatable));
            }

            if (component instanceof IIteratorRepeatable && ((IIteratorRepeatable) component).isComponentFactorySupported()) {
                processIteratorRepeatable((IIteratorRepeatable) component, initSection, declarationSection);
            }

            processFieldsWithAnnotation(targetClass, component, CompilationTraversable.class, initSection, declarationSection, false);
            processFieldsWithAnnotation(targetClass, component, XMLMetadataSubelements.class, initSection, declarationSection, splitSection);
            processFieldsWithAnnotation(targetClass, component, XMLMetadataSubelement.class, initSection, declarationSection, splitSection);
            processFieldsWithAnnotation(targetClass, component, XMLMetadataSubelementParent.class, initSection, declarationSection, false);

            if (isNotSupportedIterable) {
                notSupportedIteratorComponents.remove(component);
            }

            if (splitSection) {
                initSection.addLineWithIndent(0, "}");
            }

            if (component instanceof Generable) {
                addIncludedActions((Generable)component);
            }
        } catch (FhFormGeneratorException e) {
            throw e; // not wrapping needed
        } catch (Throwable e) {
            // wrap with generation exception to point faulty component
            throw new FhFormGeneratorException(getIdForException(component),
                    "Exception while processing component " + component.getType() + ", id = " + component.getId(), e);
        }
    }

    private void addIncludedActions(Generable component) {
        Form includedForm = component.generateForm();
        if (includedForm != null) {
            includedForm.setGroupingParentComponent(null);
            processComponentActions(includedForm);
        }
    }

    private void processComponentActions(Component component) {
        try {
            // avoid recursive processing aleady processed component
            ComponentWrapper componentWrapper = new ComponentWrapper(component);
            if (alreadyProcessedComponents.contains(componentWrapper)) {
                return;
            }

            alreadyProcessedComponents.add(componentWrapper);

            // if within IIndexedBindingOwner, push iterators to context
            List<CompiledRepeatableIterator> optionalIndexedBindingIterators = null;
            if (component instanceof IIndexedBindingOwner) {
                optionalIndexedBindingIterators = pushAndGetIteratorsForIndexBinding((IIndexedBindingOwner) component);
            }

            Class targetClass = component.getClass();
            // @XMLProperty
            for (Field field : ReflectionUtils.getFieldsWithHierarchy(targetClass, XMLProperty.class)) {
                XMLProperty attrAnnotation = field.getAnnotation(XMLProperty.class);
                if (attrAnnotation.skipCompiler()) {
                    continue;
                }
                Object fieldValue = getGetterValue(component, targetClass, field, XMLProperty.class);
                if (fieldValue == null) {
                    continue;
                }
                if (fieldValue instanceof AdHocActionBinding) {
                    assignFieldName(component);
                    getBindingGenerator().createActionBindingLiteral((AdHocActionBinding) fieldValue, component, field, allActions);
                }
            }


            // if within IIndexedBindingOwner, pop iterators from context
            if (optionalIndexedBindingIterators != null) {
                popIterators(optionalIndexedBindingIterators);
            }

            // subcomponents
            if (component instanceof IGroupingComponent) {
                IGroupingComponent<Component> castedGroup = (IGroupingComponent<Component>) component;
                castedGroup.getSubcomponents().forEach(this::processComponentActions);
            }

            if (component instanceof IIteratorRepeatable && ((IIteratorRepeatable) component).isComponentFactorySupported()) {
                ((IIteratorRepeatable)component).getRepeatedComponents().forEach(this::processComponentActions);
            }
        } catch (FhFormGeneratorException e) {
            throw e; // not wrapping needed
        } catch (Throwable e) {
            // wrap with generation exception to point faulty component
            throw new FhFormGeneratorException(getIdForException(component),
                    "Exception while processing component " + component.getType() + ", id = " + component.getId(), e);
        }
    }

    private List<CompiledRepeatableIterator> pushAndGetIteratorsForRepeatable(IIteratorRepeatable<?> repeatable) {
        List<IRepeatableIteratorInfo> iterators = repeatable.getIteratorInfos();
        List<CompiledRepeatableIterator> newIterators = new ArrayList<>();

        ExpressionContext newExpressionContext = new ExpressionContext(getCurrentBindingContext());
        // push new binding context at this point to already use it
        currentExpressionContexts.push(newExpressionContext);

        for (IRepeatableIteratorInfo iter : iterators) {
            // check for duplicate (in this or parent repeaters)
            if (currentIterators.stream().anyMatch(oldIter -> iter.getName().equals(oldIter.getName()))) {
                throw new FhFormGeneratorException(getIdForException((Component) repeatable),
                        "Iterator '" + iter.getName() + "' already exists in components stack");
            } else {
                String iterName = iter.getName();
                String iterCollection = StringUtils.removeSurroundingBraces(iter.getCollectionBinding());
                if (iterCollection == null) {
                    throw new FhFormGeneratorException(getIdForException((Component) repeatable.getIteratorDefiningComponent()),
                            "Collection binding must be defined for iterator '" + iterName + "'");
                }
                String indexVar = reserveFieldName(iterName + "Index");
                String iterIndexExpr = iterName + "$index";


                // only first iterator can have a row number offset
                boolean hasRowOffset = iterators.get(0) == iter;
                String rowNoOffsetSuplVar = null;
                String rowNoOffsetSuplExpr = "";
                if (hasRowOffset) {
                    rowNoOffsetSuplVar = reserveFieldName(iterName + "RowNoOffset");
                    rowNoOffsetSuplExpr = " + " + rowNoOffsetSuplVar + ".getRowNumberOffset()";
                    newExpressionContext.addBindingRootAsParameter(rowNoOffsetSuplVar, IRowNumberOffsetSupplier.class);
                }

                // add index from factory param as param in binding context
                newExpressionContext.addBindingRootAsParameterWithOtherName(indexVar, int.class, iterIndexExpr);

                // add rowNumber and index expression roots
                AbstractExpressionProcessor.AccessorExpression rowNoExpression = getBindingGenerator().createExecutorOrGetter(
                        "T(java.lang.String).valueOf(" + iterIndexExpr + " + 1" + rowNoOffsetSuplExpr + ")",
                        reserveFieldName(firstLetterToUpper(iterName) + "IteratorRowNo"),
                        getCurrentBindingContext(), newExpressionContext.getAllParametersAsArray());
                newExpressionContext.addBindingRoot(iterName + "$rowNo", rowNoExpression.getExpression(), String.class);

                // create getter for collection element that uses collection index
                AbstractExpressionProcessor.AccessorExpression getterExpression = getBindingGenerator().createExecutorOrGetter(
                        iterCollection + "[" + iterIndexExpr + "]", reserveFieldName(firstLetterToUpper(iterName) + "Iterator"),
                        getCurrentBindingContext(), newExpressionContext.getAllParametersAsArray());

                // add getter as iterator replacement in binding context
                newExpressionContext.addBindingRoot(iterName, getterExpression.getExpression(), getterExpression.getType());

                // add to current iterators
                CompiledRepeatableIterator newIter = new CompiledRepeatableIterator(iterName, iterCollection, indexVar, rowNoOffsetSuplVar, getterExpression);
                newIterators.add(newIter);
                currentIterators.add(newIter);
            }
        }

        return newIterators;
    }

    private List<CompiledRepeatableIterator> pushAndGetIteratorsForIndexBinding(IIndexedBindingOwner owner) {
        List<IRepeatableIteratorInfo> iterators = owner.getIteratorInfos();
        List<CompiledRepeatableIterator> newIterators = new ArrayList<>();

        ExpressionContext newExpressionContext = new ExpressionContext(getCurrentBindingContext());
        // push new binding context at this point to already use it
        currentExpressionContexts.push(newExpressionContext);

        // add index from factory param as param in binding context
        newExpressionContext.addBindingRootAsParameter(IIndexedBindingOwner.INDICES_ARRAY_PARMETER_NAME, int[].class);

        int iteratorIndex = 0;
        for (IRepeatableIteratorInfo iter : iterators) {
            // check for duplicate (in this or parent repeaters)
            if (currentIterators.stream().anyMatch(oldIter -> iter.getName().equals(oldIter.getName()))) {
                throw new FhFormGeneratorException(getIdForException((Component) owner),
                        "Iterator '" + iter.getName() + "' already exists in components stack");
            } else {
                String iterName = iter.getName();
                String iterCollection = StringUtils.removeSurroundingBraces(iter.getCollectionBinding());

                String getterSpel = String.format("%s[%s[%d]]",
                        iterCollection, IIndexedBindingOwner.INDICES_ARRAY_PARMETER_NAME, iteratorIndex);

                // create getter for collection element that uses iterator indexes array and fixed index of iterator in this array
                AbstractExpressionProcessor.AccessorExpression getterExpression = getBindingGenerator().createExecutorOrGetter(
                        getterSpel, reserveFieldName(firstLetterToUpper(iterName) + "Iterator"),
                        getCurrentBindingContext(), newExpressionContext.getAllParametersAsArray());

                // add getter as iterator replacement in binding context
                newExpressionContext.addBindingRoot(iterName, getterExpression.getExpression(), getterExpression.getType());

                // add to current iterators
                CompiledRepeatableIterator newIter = new CompiledRepeatableIterator(iterName, iterCollection, null, null, getterExpression);
                newIterators.add(newIter);
                currentIterators.add(newIter);

                iteratorIndex++;
            }
        }

        return newIterators;
    }

    private void popIterators(List<CompiledRepeatableIterator> iterators) {
        // remove binding context with these iterators
        currentExpressionContexts.pop();
        // remove them from list
        currentIterators.removeAll(iterators);
    }

    private void processFieldsWithAnnotation(Class<?> targetClass, Component component,
                                             Class<? extends Annotation> annotationClass,
                                             GenerationContext initSection, GenerationContext declarationSection, boolean splitSection) {
        // @CompilationTraversable subcomponents
        for (Field field : ReflectionUtils.getFieldsWithHierarchy(targetClass, annotationClass)) {
            Object embeddedComponentOrCollection = getGetterValue(component, targetClass, field, annotationClass);

            if (embeddedComponentOrCollection == null) {
                continue;
            }

            if (embeddedComponentOrCollection instanceof Component) {
                if (componentFilteredOut((Component) embeddedComponentOrCollection)) {
                    continue;
                }
                // get or assign a new field name
                String embeddedComponentFieldName = declareEmbeddedComponentField((Component) embeddedComponentOrCollection, initSection, declarationSection);

                // wire with parent
                initSection.addLine("%s.set%s(%s);", resolveName(component), firstLetterToUpper(field.getName()), embeddedComponentFieldName);
                processComponent((Component) embeddedComponentOrCollection, initSection, declarationSection, splitSection);
            } else if (embeddedComponentOrCollection instanceof Collection
                    && Component.class.isAssignableFrom(ReflectionUtils.getGenericTypeInFieldType(field, 0))) {

                for (Component embeddedComponent : (Collection<Component>) embeddedComponentOrCollection) {
                    if (componentFilteredOut(embeddedComponent)) {
                        continue;
                    }
                    // get or assign a new field name
                    String embeddedComponentFieldName = declareEmbeddedComponentField(embeddedComponent, initSection, declarationSection);

                    // wire with parent
                    initSection.addLine("%s.get%s().add(%s);", resolveName(component), firstLetterToUpper(field.getName()), embeddedComponentFieldName);
                    processComponent((Component) embeddedComponent, initSection, declarationSection, splitSection);
                }
            } else {
                throw new FhFormGeneratorException(getIdForException(component), String.format(
                        "Field %s.%s is annotated with %s but is not a Collection<? extends %s> nor a %s",
                        field.getDeclaringClass().getName(), field.getName(),
                        annotationClass.getSimpleName(),
                        Component.class.getSimpleName(), Component.class.getSimpleName()));
            }
        }
    }

    private String declareEmbeddedComponentField(Component embeddedComponent,
                                                 GenerationContext initSection,
                                                 GenerationContext declarationSection) {

        // get or assign a new field name
        if (hasAssignedName(embeddedComponent)) {
            return resolveName(embeddedComponent);
        } else {
            Class<?> embeddedComponentClass = embeddedComponent.getClass();
            String embeddedComponentFieldName;
            // check if java form contains fields that this component should be injected to
            if (componentInjectionDeclarations.containsKey(embeddedComponent.getId())) {
                Field injectionField = componentInjectionDeclarations.get(embeddedComponent.getId());
                embeddedComponentFieldName = injectionField.getName();
                if (!currentIterators.isEmpty()) {
                    throw new FhFormGeneratorException(embeddedComponent.getId(), String.format(
                            "Component %s(\"%s\") is inside iterator and cannot be injected (injection field %s.%s)",
                            embeddedComponentClass.getSimpleName(), embeddedComponent.getId(),
                            injectionField.getDeclaringClass().getName(), embeddedComponentFieldName));
                }
                if (!injectionField.getType().isAssignableFrom(embeddedComponentClass)) {
                    throw new FhFormGeneratorException(embeddedComponent.getId(), String.format(
                            "Component injection field %s.%s has invalid type. Expected %s, found %s",
                            injectionField.getDeclaringClass().getName(), embeddedComponentFieldName,
                            embeddedComponentClass.getName(), injectionField.getType().getName()));
                }

                // reuse existing (injection point) field
                addFixedFieldMapping(embeddedComponentFieldName, embeddedComponent);
                // remove from declaration map - entries left after processing all components will indicate inexisting components
                componentInjectionDeclarations.remove(embeddedComponent.getId());
            } else {
                embeddedComponentFieldName = assignFieldName(embeddedComponent);
                // field declaration
                declarationSection.addLine();
                declarationSection.addLine("%s %s;", toTypeLiteral(embeddedComponentClass), embeddedComponentFieldName);
            }

            initSection.addLine();
            initSection.addLine("%s = new %s(%s);", embeddedComponentFieldName, toTypeLiteral(embeddedComponentClass), resolveName(form));
            return embeddedComponentFieldName;
        }
    }

    private void processSubcomponents(Component group, List<Component> components,
                                      GenerationContext initSection, GenerationContext declarationSection,
                                      boolean addToParent, Consumer<Component> postInitCompoment, boolean splitSection) {
        for (Component component : components) {
            if (componentFilteredOut(component)) {
                continue;
            }
            String componentFieldName = declareEmbeddedComponentField(component, initSection, declarationSection);
            if (addToParent) {
                initSection.addLine("%s.addSubcomponent(%s);", resolveName(group), componentFieldName);
            }
            initSection.addLine("%s.setGroupingParentComponent(%s);", componentFieldName, resolveName(group));

            // setters on fields + subcomponents
            processComponent(component, initSection, declarationSection, splitSection);

            if(postInitCompoment != null) {
                postInitCompoment.accept(component);
            }
        }
    }

    private void processIteratorRepeatable(IIteratorRepeatable<?> repeatable, GenerationContext initSection, GenerationContext declarationSection) {
        // TODO: remove
        if (!notSupportedIteratorComponents.isEmpty()) {
            processSubcomponents((Component) repeatable, (List) repeatable.getRepeatedComponents(), initSection, declarationSection, true, null, true);
            return;
        }

        // extract iterators and push them do current iterators
        List<CompiledRepeatableIterator> iterators = pushAndGetIteratorsForRepeatable(repeatable);
        String idSuffixExpression = buildIdSuffixExpressionFromIterators();

        // resolve repeater name
        String repeatableGlobalName = resolveName(repeatable);

        // assign a new local alias of grouping component to be used inside the block of code
        String groupingComponentLocalName = assignLocalAlias(repeatableGlobalName + "GroupingComponent", repeatable.getGroupingComponentForNewComponents());

        // add factory setter beginnig lambda line
        CompiledRepeatableIterator mainIter;
        if (iterators.size() == 0) {
            mainIter = new CompiledRepeatableIterator();
            mainIter.setRowNoOffsetSupplierVar("rowNo");
        }
        else {
            mainIter = iterators.get(0);
        }
        if (repeatable instanceof IMultipleIteratorRepeatable) {
            String indicesLocalName = reserveFieldName(repeatableGlobalName + "Indices");
            initSection.addLine("%s.setInteratorComponentFactory((%s, %s, %s) -> {", repeatableGlobalName,
                    groupingComponentLocalName, mainIter.getRowNoOffsetSupplierVar(), indicesLocalName);

            // explode iterator index array to local var per iterator
            int iteratorIndex = 0;
            for (CompiledRepeatableIterator iterator : iterators) {
                initSection.addLineWithIndent(1, "int %s = %s[%s]; // %s index", iterator.getIteratorIndexVar(),
                        indicesLocalName, String.valueOf(iteratorIndex), iterator.getName());
                iteratorIndex++;
            }
        } else if (repeatable instanceof ISingleIteratorRepeatable) {
            initSection.addLine("%s.setInteratorComponentFactory((%s, %s, %s) -> {", repeatableGlobalName,
                    groupingComponentLocalName, mainIter.getRowNoOffsetSupplierVar(), mainIter.getIteratorIndexVar());
        } else {
            throw new FhFormGeneratorException(getIdForException((Component) repeatable), "Not supported " + repeatable.getClass().getName());
        }

        // create new sections with indent
        GenerationContext subDeclarationSection = new GenerationContext();
        GenerationContext subInitSection = new GenerationContext();

        // add both sections to init section - local variables will be created
        initSection.addSection(subDeclarationSection, 1);
        initSection.addSection(subInitSection, 1);

        // process subcomponents
        processSubcomponents((Component) repeatable, (List) repeatable.getRepeatedComponents(), subInitSection, subDeclarationSection, false,
                component -> {
                    subInitSection.addLine("%s.addSuffixToIdWithSubcomponents(%s, %s);",
                            toTypeLiteral(CompiledClassesHelper.class), resolveName(component), idSuffixExpression);
                    subInitSection.addLine("%s.initWithSubcomponents(%s);", toTypeLiteral(CompiledClassesHelper.class), resolveName(component));
                    if (component.getId() != null) {
                        subInitSection.addLine("%s.assignAvailabilityRulesForRepeatedComponent(%s, %s);",
                                toTypeLiteral(CompiledClassesHelper.class),
                                toStringLiteral(component.getId()), resolveName(component));
                    }
                }, false);

        // return components
        String componentFields = repeatable.getRepeatedComponents().stream()
                .filter(comp -> !componentFilteredOut(comp))
                .map(comp -> resolveName(comp)).collect(Collectors.joining(", "));
        subInitSection.addLine("return %s.asList(%s);", toTypeLiteral(Arrays.class), componentFields);

        // restore previous grouping component's name
        unassignLocalAlias(repeatable.getGroupingComponentForNewComponents());

        // finish factory setter
        initSection.addLine("});");

        // pop iterators from stack
        popIterators(iterators);
    }

    private void addFormActionSignatures() {
        fieldSection.addLine("public static final %s<%s> %s = new %s<>();",
                toTypeLiteral(Set.class), toTypeLiteral(ActionSignature.class),
                FormsManager.FORM_ACTIONS_FIELD, toTypeLiteral(LinkedHashSet.class));

        fieldSection.addLine("static {");
        for (ActionSignature action : allActions) {
            StringBuilder argumentTypesLiteral = new StringBuilder();
            for (Type argumentType : action.getArgumentTypes()) {
                if (Collection.class.isAssignableFrom(ReflectionUtils.getRawClass(argumentType)) &&
                        ReflectionUtils.getGenericArgumentsRawClasses(argumentType).length > 0) {
                    argumentTypesLiteral.append(String.format(", %s.createCollectionType(%s.class, %s.class)",
                            toTypeLiteral(CompiledClassesHelper.class),
                            toTypeLiteral(ReflectionUtils.getRawClass(argumentType)),
                            toTypeLiteral(ReflectionUtils.getGenericArgumentsRawClasses(argumentType)[0])));
                } else {
                    argumentTypesLiteral.append(String.format(", %s.class",
                            toTypeLiteral(ReflectionUtils.getRawClass(argumentType))));
                }
            }

            fieldSection.addLineWithIndent(1, "%s.add(new %s(%s%s));", FormsManager.FORM_ACTIONS_FIELD,
                    toTypeLiteral(ActionSignature.class), toStringLiteral(action.getActionName()), argumentTypesLiteral.toString());
        }
        fieldSection.addLine("}\n");
    }

    private void addFormStaticIncludes() {
        fieldSection.addLine("public static final %s<%s> %s = new %s<>();",
                toTypeLiteral(Set.class), toTypeLiteral(String.class),
                FormsManager.FORM_INCLUDES_FIELD, toTypeLiteral(LinkedHashSet.class));

        fieldSection.addLine("static {");
        form.doActionForEverySubcomponent(component -> {
                if (component instanceof Includeable) {
                    String includedForm = ((Includeable) component).getStaticRef();
                    if (!StringUtils.isNullOrEmpty(includedForm)) {
                        fieldSection.addLineWithIndent(1, "%s.add(\"%s\");", FormsManager.FORM_INCLUDES_FIELD,
                                includedForm);
                    }
                }
            }
        );
        fieldSection.addLine("}\n");
    }

    private void addFormVariants() {
        fieldSection.addLine("public static final %s<%s> %s = new %s<>();",
                toTypeLiteral(Set.class), toTypeLiteral(String.class),
                FormsManager.FORM_VARIANTS_FIELD, toTypeLiteral(LinkedHashSet.class));

        if (form.getAvailabilityConfiguration() != null) {
            fieldSection.addLine("static {");
            form.getAvailabilityConfiguration().getSettings().stream().
                    filter(Variant.class::isInstance).
                    map(Variant.class::cast).forEach(variant -> {
                fieldSection.addLineWithIndent(1, "%s.add(\"%s\");", FormsManager.FORM_VARIANTS_FIELD,
                        variant.getId());
            });
            fieldSection.addLine("}\n");
        }
    }


    protected BindingJavaCodeGenerator getBindingGenerator() {
        List<ITypeProvider> typeProviders = new ArrayList<>();

        if (internalModelTypeProvider != null) {
            typeProviders.add(internalModelTypeProvider);
        }
        if (rulesTypeProvider != null) {
            typeProviders.add(rulesTypeProvider);
        } else {
            FhLogger.warn("Rule's type provider not present");
        }
        if (enumsTypeDependencyProvider != null) {
            typeProviders.add(enumsTypeDependencyProvider);
        } else {
            FhLogger.warn("Enum's type provider not present");
        }
        return new BindingJavaCodeGenerator(this, getCurrentBindingContext(), typeProviders.toArray(new ITypeProvider[typeProviders.size()]));
    }

    protected ExpressionContext getCurrentBindingContext() {
        return currentExpressionContexts.peek();
    }

    private String buildIdSuffixExpressionFromIterators() {
        if (currentIterators.isEmpty()) {
            return toStringLiteral("noIteratorsProvided");
        } else {
            return currentIterators.stream()
                    .map(iter -> String.format("\"_%s_\" + %s", iter.getName(), iter.getIteratorIndexVar()))
                    .collect(Collectors.joining(" + "));
        }
    }

    // TODO: remove me after implementing Tree iterator
    private boolean isNotSupportedIterable(Component component) {
        if (component.getClass().getAnnotation(CompilationNotSupportedIterable.class) != null) {
            return true;
        }
        IGroupingComponent<?> parent = component.getGroupingParentComponent();
        while (parent != null) {
            if (parent.getClass().getAnnotation(CompilationNotSupportedIterable.class) != null) {
                return true;
            }
            parent = Component.class.cast(parent).getGroupingParentComponent();
        }

        return false;
    }

    private boolean componentFilteredOut(Component component) {
        return componentFilter.isPresent() && !componentFilter.get().test(component);
    }

    private void buildInjectedComponentsMap() {
        Class<?> formClass = form.getClass();
        while (formClass != Form.class) {
            for (Field field : formClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(InjectComponent.class)) {
                    InjectComponent injection = field.getAnnotation(InjectComponent.class);
                    String componentId = injection.value();
                    if (componentId.isEmpty()) {
                        componentId = field.getName();
                    }

                    if (!Modifier.isProtected(field.getModifiers())
                        && !Modifier.isPublic(field.getModifiers())) {
                        throw new FhFormGeneratorException(componentId, String.format(
                                "Component injection field %s.%s must be declared protected or public",
                                formClass.getName(), field.getName()));
                    }
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new FhFormGeneratorException(componentId, String.format(
                                "Component injection field %s.%s must NOT be static",
                                formClass.getName(), field.getName()));
                    }
                    if (Modifier.isFinal(field.getModifiers())) {
                        throw new FhFormGeneratorException(componentId, String.format(
                                "Component injection field %s.%s must NOT be final",
                                formClass.getName(), field.getName()));
                    }
                    componentInjectionDeclarations.put(componentId, field);
                }
            }
            formClass = formClass.getSuperclass();
        }
    }

    private void validateNotInjectedComponents() {
        // satisfied injection requests are removed from declaration map - entries left after processing all components will indicate inexisting components
        if (!componentInjectionDeclarations.isEmpty()) {
            String msg = componentInjectionDeclarations.entrySet().stream()
                    .map(f -> f.getValue().getDeclaringClass().getName() + "." + f.getValue().getName() + " -> " + f.getKey()) // class.field -> componentId
                    .collect(Collectors.joining(", "));
            throw new FhFormGeneratorException(null, "Unsatisfied component injection requests: " + msg);
        }
    }
}
