package pl.fhframework.compiler.core.generator;

import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.ast.SpelNodeImpl;
import org.springframework.expression.spel.ast.TypeReference;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.Action;
import pl.fhframework.annotations.CompilationNotSupportedIterable;
import pl.fhframework.binding.*;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.forms.iterators.IIndexedBindingOwner;
import pl.fhframework.core.generator.CompiledClassesHelper;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.events.ViewEvent;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.IGroupingComponent;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static pl.fhframework.core.util.StringUtils.removeSurroundingBraces;

/**
 * Binding java code generator. This class is not thread-safe.
 */
public class BindingJavaCodeGenerator extends ExpressionJavaCodeGenerator {

    public static final String CONVERSION_SERVICE_GETTER = "__getConversionService";
    public static final String MESSAGES_SERVICE_GETTER = "__getMessageService";

    private AbstractJavaCodeGenerator codeGenerator;

    private static final String PART_SUFFIX = "_part";

    private boolean useCompiledBindings = true; // TODO: change to true

    public BindingJavaCodeGenerator(AbstractJavaClassCodeGenerator codeGenerator, ExpressionContext globalExpressionContext, ITypeProvider... typeProviders) {
        super(codeGenerator.getMethodSection(), globalExpressionContext, typeProviders);
        this.codeGenerator = codeGenerator;
    }

    // new CompiledIndexedModelBinding(this::getFullModelPath)
    public String createIndexedBindingLiteral(AdHocIndexedModelBinding<?> binding, Component component, Field field) {
        Class<?> targetBindingType = ReflectionUtils.getGenericTypeInFieldType(field, 0);

        // ad hoc binding
        if (!useCompiledBinding(component, field)) {
            return createAdHocBindingLiteral(component, binding.getBindingExpression());
        }

        IIndexedBindingOwner indexedBindingOwner = (IIndexedBindingOwner) component;

        String bindingExpression = binding.getBindingExpression();
        String suggestedMethodName = suggestMethodName(component, field);
        try {
            String expressionText = removeSurroundingBraces(binding.getBindingExpression());
            List<SpelNodeImpl> parts = parseExpression(expressionText);

            // create getter expression without wrapping with accessor method
            AccessorExpression indexedGetter = createCompiledAccessor(expressionText, parts, suggestedMethodName, AccessorType.GETTER,
                    globalExpressionContext, true, globalExpressionContext.getAllParameters());

            return createCompiledIndexedBindingLiteralFromAccessor(bindingExpression, indexedGetter);
        } catch (RuntimeException e) {
            String msg = format("Exception while compiling binding expression '%s' for %s.%s in component with id %s",
                    bindingExpression, component.getClass().getSimpleName(), field.getName(),
                    component.getId() != null ? component.getId() : "<no id assigned>");
            throw new FhFormException(msg, e);
        }
    }

    // new CompiledBinding(this::getFullModelPath, this::setFullModelPath)
    public String createBindingLiteral(AdHocModelBinding<?> binding, Component component, Field field) {
        Class<?> targetBindingType = ReflectionUtils.getGenericTypeInFieldType(field, 0);

        // static value
        if (binding.isStaticValue()) {
            return createStaticBindingLiteral(binding.getStaticValueText(), targetBindingType);
        }

        // ad hoc binding
        if (!useCompiledBinding(component, field)) {
            return createAdHocBindingLiteral(component, binding.getBindingExpression());
        }

        if (binding.getBindingExpressionError() != null) {
            throw new FhFormException(format("Invalid binding %s in %s property of %s component with id %s: %s",
                    binding.getBindingExpression(), field.getName(), component.getClass().getSimpleName(),
                    component.getId() != null ? component.getId() : "<not set in XML>",
                    binding.getBindingExpressionError()));
        }
        String bindingExpression = binding.getBindingExpression();
        String suggestedMethodName = suggestMethodName(component, field);

        try {
            AccessorExpression getter;
            AccessorExpression setter;
            AccessorExpression baseObjectGetter;

            if (binding.isCombined()) { // combined (static + binding)
                int partCounter = 1;
                List<String> expressionLiterals = new ArrayList<>();
                for (AdHocModelBinding.CombinedExpression expression : binding.getCombinedExpressions()) {
                    String singleExpression;
                    if (expression.isBinding()) {
                        singleExpression = createCompiledAccessor(
                                expression.getValue(),
                                parseExpression(expression.getValue()),
                                suggestedMethodName + PART_SUFFIX + partCounter++, // add suffix to produce different method names
                                AccessorType.GETTER,
                                globalExpressionContext, false, globalExpressionContext.getAllParameters()).expression; // start with model accessor and type
                        singleExpression = format("%s.nvl(%s)", toTypeLiteral(CompiledClassesHelper.class), singleExpression); //add nvl(expression)
                    } else {
                        singleExpression = AbstractJavaCodeGenerator.toStringLiteral(expression.getValue());
                    }
                    expressionLiterals.add(singleExpression);
                }

                // create getter as sum of Strings from single expressions of this combined expression
                getter = addAccessorMethod(bindingExpression, suggestedMethodName, String.class,
                        StringUtils.joinWithoutEmpty(expressionLiterals, " + "), NO_PROPERTY, AccessorType.GETTER, globalExpressionContext.getAllParameters());

                // combined -> no setter possible
                setter = null;
                // combined -> no single base object
                baseObjectGetter = null;
            } else { // single binding
                Class<?> targetType = Object.class;
                String expressionText = removeSurroundingBraces(binding.getBindingExpression());
                List<SpelNodeImpl> parts = parseExpression(expressionText);

                if (useBaseObject(parts, globalExpressionContext)) {
                    // create getter for the beginning of the expr.
                    baseObjectGetter = createCompiledAccessor(expressionText, CollectionsUtils.getWithoutLast(parts),
                            suggestedMethodName + BASE_GETTER_SUFFIX, AccessorType.GETTER, globalExpressionContext, true,
                            globalExpressionContext.getAllParameters());

                    ExpressionContext baseObjectContext = new ExpressionContext(globalExpressionContext);
                    baseObjectContext.setDefaultBindingRootAsParameter(BASE_OBJECT_ARG_NAME, baseObjectGetter.type);

                    getter = createCompiledAccessor(expressionText, CollectionsUtils.asNewList(CollectionsUtils.getLast(parts)),
                            suggestedMethodName, AccessorType.GETTER, baseObjectContext, true, baseObjectContext.getAllParameters());

                    setter = createCompiledAccessor(expressionText, CollectionsUtils.asNewList(CollectionsUtils.getLast(parts)),
                            suggestedMethodName, AccessorType.SETTER, baseObjectContext, true, baseObjectContext.getAllParameters());
                } else {
                    // no base object
                    baseObjectGetter = null;

                    getter = createCompiledAccessor(expressionText, new ArrayList<>(parts), suggestedMethodName, AccessorType.GETTER,
                            globalExpressionContext, true, globalExpressionContext.getAllParameters());

                    setter = createCompiledAccessor(expressionText, new ArrayList<>(parts), suggestedMethodName, AccessorType.SETTER,
                            globalExpressionContext, true, globalExpressionContext.getAllParameters());
                }
            }

            return createCompiledBindingLiteralFromAccessors(bindingExpression, baseObjectGetter, getter, setter);
        } catch (RuntimeException e) {
            String msg = format("Exception while compiling binding expression '%s' for %s.%s in component with id %s",
                    bindingExpression, component.getClass().getSimpleName(), field.getName(),
                    component.getId() != null ? component.getId() : "<no id assigned>");
            throw new FhFormException(msg, e);
        }
    }

    // new CompiledActionBinding(...)
    public String createActionBindingLiteral(AdHocActionBinding actionBinding, Component component, Field field, Set<ActionSignature> actions) {
        // ad hoc binding
        if (!useCompiledBinding(component, field)) {
            return format("new %s(%s, %s, %s)",
                    toTypeLiteral(AdHocActionBinding.class),
                    AbstractJavaCodeGenerator.toStringLiteral(actionBinding.getActionBindingExpression()),
                    "this",
                    codeGenerator.resolveName(component));
        }

        StringBuilder argumentListLiteral = new StringBuilder();
        String suggestedMethodPrefix = suggestMethodName(component, field);
        ExpressionContext argumentExpressionContext = new ExpressionContext(globalExpressionContext);
        argumentExpressionContext.addBindingRootAsParameterWithOtherName("event", ViewEvent.class, ActionBinding.EVENT_KEYWORD, ActionBinding.EVENT_KEYWORD_OLD);

        Type[] argumentTypes = new Type[actionBinding.getArguments().length];
        for (int i = 0; i < actionBinding.getArguments().length; i++) {
            ActionBinding.ActionArgument argumentDefinition = actionBinding.getArguments()[i];
            String suggestedMethodName = suggestedMethodPrefix + "_arg" + (i + 1);
            List<SpelNodeImpl> parts = parseExpression(argumentDefinition.getBindingExpression());

            AccessorExpression valueGetter = createCompiledAccessor(argumentDefinition.getBindingExpression(), parts,
                    suggestedMethodName, AccessorType.GETTER,
                    argumentExpressionContext, true, argumentExpressionContext.getAllParameters());

            // generic collection literal
            argumentTypes[i] = valueGetter.getType();
            argumentListLiteral.append(format(",\n        new %s(%s, %s) /* %s */",
                    toTypeLiteral(ActionBinding.ActionArgument.class),
                    AbstractJavaCodeGenerator.toStringLiteral(argumentDefinition.getBindingExpression()),
                    toLambda(valueGetter, "event"),
                    argumentDefinition.getBindingExpression()));
        }

        if (!Action.NO_ACTION_DEFAULT.equals(actionBinding.getActionName()) && !Action.NO_ACTION_WITH_VALIDATION.equals(actionBinding.getActionName())) {        // add signature to set of signatures
            actions.add(new ActionSignature(actionBinding.getActionName(), argumentTypes));
        }

        return format("new %s(\n        %s, %s%s)",
                toTypeLiteral(CompiledActionBinding.class),
                AbstractJavaCodeGenerator.toStringLiteral(actionBinding.getActionBindingExpression()),
                AbstractJavaCodeGenerator.toStringLiteral(actionBinding.getActionName()),
                argumentListLiteral);
    }

    private String createCompiledBindingLiteralFromAccessors(String bindingExpression,
                                                             AccessorExpression baseObjectGetter,
                                                             AccessorExpression valueGetter,
                                                             AccessorExpression valueSetter) {

        if (baseObjectGetter != null) {
            return format("new %s<>(\n" +
                            "        %s,\n" +
                            "        %s, /* property name */\n" +
                            "        %s.class, /* target type */\n" +
                            "        this::%s,\n" +
                            "        %s, /* base object getter */\n" +
                            "        %s, /* getter of property on base object */\n" +
                            "        %s /* setter of property on base object */\n" +
                            "    )",
                    toTypeLiteral(CompiledBinding.class),
                    AbstractJavaCodeGenerator.toStringLiteral(bindingExpression),
                    AbstractJavaCodeGenerator.toStringLiteral(valueGetter.property),
                    toTypeLiteral(ReflectionUtils.getRawClass(valueGetter.type)),
                    CONVERSION_SERVICE_GETTER,
                    toLambda(baseObjectGetter),
                    toLambda(valueGetter, BASE_OBJECT_ARG_NAME),
                    toLambda(valueSetter, BASE_OBJECT_ARG_NAME, SETTER_ARG_NAME));
        } else {
            return format("new %s<>(\n" +
                            "        %s,\n" +
                            "        %s,\n" +
                            "        %s.class, /* target type */\n" +
                            "        this::%s,\n" +
                            "        %s, /* getter */ \n" +
                            "        %s /* setter */\n" +
                            "    )",
                    toTypeLiteral(CompiledBinding.class),
                    AbstractJavaCodeGenerator.toStringLiteral(bindingExpression),
                    AbstractJavaCodeGenerator.toStringLiteral(valueGetter.property),
                    toTypeLiteral(ReflectionUtils.getRawClass(valueGetter.type)),
                    CONVERSION_SERVICE_GETTER,
                    toLambda(valueGetter),
                    toLambda(valueSetter, SETTER_ARG_NAME));
        }
    }

    private String createCompiledIndexedBindingLiteralFromAccessor(String bindingExpression, AccessorExpression valueIndexedGetter) {
        return format("new %s<>(\n" +
                        "        %s,\n" +
                        "        %s /* indices based getter */ \n" +
                        "    )",
                toTypeLiteral(CompiledIndexedModelBinding.class),
                AbstractJavaCodeGenerator.toStringLiteral(bindingExpression),
                toLambda(valueIndexedGetter, IIndexedBindingOwner.INDICES_ARRAY_PARMETER_NAME));
    }

    private String createAdHocBindingLiteral(Component component, String bindingExpression) {
        return format("new %s<>(%s, %s, %s)",
                toTypeLiteral(AdHocModelBinding.class),
                "this",
                codeGenerator.resolveName(component),
                AbstractJavaCodeGenerator.toStringLiteral(bindingExpression));
    }

    private String createAdHocIndexedBindingLiteral(Component component, String bindingExpression) {
        return format("new %s<>(%s, %s, (%s) %s)",
                toTypeLiteral(AdHocIndexedModelBinding.class),
                AbstractJavaCodeGenerator.toStringLiteral(bindingExpression),
                "this",
                toTypeLiteral(IIndexedBindingOwner.class),
                codeGenerator.resolveName(component));
    }

    private String createStaticBindingLiteral(String value, Class<?> targetBindingClass) {
        String valueLiteral;
        if (value == null) {
            return "null";
        } else if (targetBindingClass != null && Long.class.isAssignableFrom(targetBindingClass)) {
            valueLiteral = value + "L";
        } else if (targetBindingClass != null && Float.class.isAssignableFrom(targetBindingClass)) {
            valueLiteral = value + "f";
        } else if (targetBindingClass != null && Double.class.isAssignableFrom(targetBindingClass)) {
            valueLiteral = value + "d";
        } else if (targetBindingClass != null && Number.class.isAssignableFrom(targetBindingClass)) {
            valueLiteral = value;
        } else if (Boolean.class == targetBindingClass) {
            valueLiteral = value.toLowerCase();
        } else if (targetBindingClass != null && targetBindingClass.isEnum()) {
            Enum enumValue;
            try {
                enumValue = Enum.valueOf((Class) targetBindingClass, value);
            } catch (IllegalArgumentException e) {
                throw new FhBindingException(value + " is not a valid value of " + targetBindingClass.getName() + " enum.");
            }
            valueLiteral = format("%s.%s", toTypeLiteral(targetBindingClass), enumValue.name());
        } else {
            valueLiteral = AbstractJavaCodeGenerator.toStringLiteral(value);
        }
        return format("new %s<>(%s)", toTypeLiteral(StaticBinding.class), valueLiteral);
    }

    private boolean useCompiledBinding(Component component, Field field) {
        if (!useCompiledBindings) {
            return false;
        }

        { // TODO: remove me after implementing repeater's bindings!!!
            if (component.getClass().getAnnotation(CompilationNotSupportedIterable.class) != null) {
                return false;
            }
            IGroupingComponent<?> parent = component.getGroupingParentComponent();
            while (parent != null) {
                if (parent.getClass().getAnnotation(CompilationNotSupportedIterable.class) != null) {
                    return false;
                }
                parent = Component.class.cast(parent).getGroupingParentComponent();
            }
        }

        return true;
    }

    protected String suggestMethodName(Component component, Field field) {
        return codeGenerator.resolveName(component) + "_" + field.getName();
    }

    private boolean useBaseObject(List<SpelNodeImpl> exprParts, ExpressionContext expressionContext) {
        if (exprParts.isEmpty()) {
            return false;
        }

        // only if the last part is a property, usage of base object + property (needed for validation) makes sense
        if (!(CollectionsUtils.getLast(exprParts) instanceof PropertyOrFieldReference)) {
            return false;
        }

        // exception for expressions based on parameters and i18n verification
        if (exprParts.get(0) instanceof PropertyOrFieldReference) {
            String firstPropertyName = PropertyOrFieldReference.class.cast(exprParts.get(0)).getName();
            if (expressionContext.hasBindingRoot(firstPropertyName)
                    && expressionContext.getBindingRoot(firstPropertyName).isPassedAsParameter()) {
                return false;
            }
            if (firstPropertyName.startsWith("$")) {   // required for i18n
                return false;
            }
        }

        // exception for direct properties of type-reference
        if (exprParts.size() >= 2 && exprParts.get(exprParts.size() - 2) instanceof TypeReference) {
            return false;
        }

        return true;
    }
}
