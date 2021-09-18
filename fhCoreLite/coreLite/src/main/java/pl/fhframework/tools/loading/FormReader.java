package pl.fhframework.tools.loading;

import org.springframework.util.StringUtils;

import pl.fhframework.core.FhException;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.forms.iterators.IIndexedBindingOwner;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.XmlAttributeReader;
import pl.fhframework.annotations.*;
import pl.fhframework.annotations.composite.Composite;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.IndexedModelBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.attribute.IComponentAttributeTypeConverter;
import pl.fhframework.service.ComponentServiceImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


// TODO: CORE_LITE_REMOVE
public class FormReader extends XMLReader<Component, Form<?>> {

    public static final String ESCAPE_BINDING_TAG_START = "![ESCAPE[";
    public static final String ESCAPE_BINDING_TAG_END = "]]";

    private static final Set<String> NON_USECASE_ACTIONS = new HashSet<>(Arrays.asList("-", "+", ""));

    private static FormReader instance = null;
    private static List<String> formComponentsPackages = Arrays.asList("pl");//We are looking for pl package by default //TODO: Read this from JVM parameters
    private static Map<String, Class<? extends Component>> tagNameToFormComponentClass;

    //private static Map<String, Class<? extends Form>> compositesClasses;
    private static Map<String, Class<? extends CompositeForm>> compositesClasses;
    private static Map<String, Class<? extends CompositeForm>> templatesToCompositeClasses;

    private static Map<Class<?>, IComponentAttributeTypeConverter<?>> perAttributeTypeConverters = new HashMap<>(); // no need for synchrnization + null values allowed

    private static Map<String, Optional<IComponentAttributeTypeConverter<?>>> perAttributeConverters = new ConcurrentHashMap<>();

    private static List<String> jsFiles;

    private static Map<Class, Optional<Constructor>> stringBasedConstructorsCache = new ConcurrentHashMap<>();

    private static Map<String, Optional<Method>> fieldSettersCache = new ConcurrentHashMap<>();

    private static Map<String, Optional<Method>> fieldGettersCache = new ConcurrentHashMap<>();

    private static Map<Class<?>, Set<Class<?>>> typeMetadataSubelementsCache = new HashMap<>();

    //private static final Map<String, KreatorObiektu> tagNameToObjectCreator = new HashMap<>();

    public static FormReader getInstance() {
        if (instance == null) {
            synchronized (FormReader.class) {
                if (instance == null) {
                    instance = new FormReader();
                }
            }
        }
        return instance;
    }

    /**
     * Tries to map template XML path to composite class.
     *
     * @param templatePath template relative path, e.g. pl/fhframework/test/TemplateForm.xml
     * @return optional composite class using this
     */
    public Optional<Class<? extends CompositeForm>> mapCompositeTemplateClass(String templatePath) {
        return Optional.ofNullable(templatesToCompositeClasses.get(templatePath.replace('/', '.').replace('\\', '.')));
    }

    public Optional<IComponentAttributeTypeConverter> getAttributeConverter(Class<?> ownerClass, Field field) {
        Optional<IComponentAttributeTypeConverter> perAttributeConverter = getAttributeDefinedConverter(ownerClass, field);
        if (perAttributeConverter.isPresent()) {
            return perAttributeConverter;
        } else {
            return getAttributeTypeConverter((Class<?>) field.getType());
        }
    }

    public Optional<IComponentAttributeTypeConverter> getAttributeTypeConverter(Class<?> attributeType) {
        return Optional.ofNullable(perAttributeTypeConverters.get(attributeType));
    }

    public Optional<IComponentAttributeTypeConverter> getAttributeDefinedConverter(Class<?> componentClass, Field field) {
        String key = componentClass.getName() + "." + field.getName();

        // ensure cache
        if (!perAttributeConverters.containsKey(key)) {
            IComponentAttributeTypeConverter<?> converter;
            XMLProperty annotation = field.getAnnotation(XMLProperty.class);
            // no annotation nor converter in annotation
            if (annotation == null || annotation.converter() == XMLProperty.NoConverter.class) {
                converter = null;
            } else {
                // check if converter type instanceof IComponentAttributeTypeConverter
                if (!IComponentAttributeTypeConverter.class.isAssignableFrom(annotation.converter())) {
                    throw new RuntimeException(String.format("%s.%s defines an attribute converter class %s which does NOT implement %s",
                            componentClass.getName(), field.getName(), annotation.converter().getName(),
                            IComponentAttributeTypeConverter.class.getSimpleName()));
                }
                // get converter supported type
                Class<?> converterSupportedType = ReflectionUtils.getGenericTypeClassInImplementedInterface(annotation.converter(), IComponentAttributeTypeConverter.class, 0);
                // check if converter supported type is exactly the same as field type
                if (!field.getType().equals(converterSupportedType)) {
                    throw new RuntimeException(String.format("%s.%s defines an attribute converter class %s which does NOT support %s but %s",
                            componentClass.getName(), field.getName(), annotation.converter().getName(),
                            field.getType().getName(), converterSupportedType.getName()));
                }
                converter = ReflectionUtils.createClassObject(annotation.converter());
            }
            perAttributeConverters.put(key, Optional.ofNullable(converter));
        }

        // get from cache
        return (Optional) perAttributeConverters.get(key);
    }

    public <T> Optional<Constructor<T>> getStringBasedConstructorConverter(Class<T> attributeType) {
        if (!stringBasedConstructorsCache.containsKey(attributeType)) {
            stringBasedConstructorsCache.put(attributeType, ReflectionUtils.findConstructor(attributeType, String.class));
        }
        return (Optional) stringBasedConstructorsCache.get(attributeType);
    }

    // temporary, todo change the way of storing generic data.
    public Map<String, Class<? extends CompositeForm>> getCompositesClasses() {
        return compositesClasses;
    }

    private FormReader() {
        try {
            if (formComponentsPackages == null) {
                formComponentsPackages = Arrays.asList("pl");
            }
            tagNameToFormComponentClass = new HashMap<>();
            HashSet<String> allJSSet = new HashSet<>();

            compositesClasses = new HashMap<>();
            templatesToCompositeClasses = new HashMap<>();
            perAttributeTypeConverters = new HashMap<>();
            perAttributeConverters = new ConcurrentHashMap<>();

            for (String formComponentPackage : formComponentsPackages) {
                List<Class<? extends Component>> discoveredFormHighLevelConponentsClasses = ReflectionUtils.getAnnotatedClasses(formComponentPackage, Control.class, Component.class);
                Set<String> notOverridenControls = new HashSet<>();
                Set<String> overridenControls = new HashSet<>();
                for (Class<? extends Component> formComponentClass : discoveredFormHighLevelConponentsClasses) {
                    String tagName = formComponentClass.getSimpleName();
                    Control formComponentJS = formComponentClass.getAnnotation(Control.class);
                    if (notOverridenControls.contains(tagName) && !formComponentJS.override() || overridenControls.contains(tagName) && formComponentJS.override()) {
                        throw new FhException("Classes: '" + formComponentClass.getName() + "' and '" + tagNameToFormComponentClass.get(tagName).getName() + "' have the same name!!!");
                    }

                    if (!overridenControls.contains(tagName)) {
                        tagNameToFormComponentClass.put(formComponentClass.getSimpleName(), formComponentClass);
                    }

                    if (!formComponentJS.override()) {
                        notOverridenControls.add(tagName);
                    } else {
                        overridenControls.add(tagName);
                    }
                }
                tagNameToFormComponentClass.forEach((tagName, formComponentClass) -> {
                    FhLogger.debug(this.getClass(), logger -> logger.log("Registering control '{}' as '{}'", formComponentClass.getName(), tagName));
                    Control formComponentJS = formComponentClass.getAnnotation(Control.class);
                    allJSSet.addAll(Arrays.asList(formComponentJS.jsFiles()));
                });

                List<Class<? extends Component>> discoveredFormConponentsClasses = ReflectionUtils.giveClassesTypeList(formComponentPackage, Component.class);
                for (Class<? extends Component> formComponentClass : discoveredFormConponentsClasses) {
                    buildMetadataOnlySubelementsCache(formComponentClass);
                }
                buildMetadataOnlySubelementsCache(Form.class);

                List<Class<? extends CompositeForm>> discoveredCompositeClasses = ReflectionUtils.getAnnotatedClasses(formComponentPackage, Composite.class, CompositeForm.class);
                for (Class<? extends CompositeForm> discoveredCompositeClass : discoveredCompositeClasses) {
                    compositesClasses.put(discoveredCompositeClass.getSimpleName(), discoveredCompositeClass);
                    Composite composite = discoveredCompositeClass.getAnnotation(Composite.class);

                    String templatePath = discoveredCompositeClass.getPackage().getName() + "." + composite.template();

                    if (templatesToCompositeClasses.containsKey(templatePath)) {
                        throw new FhFormException("Use of template in multiple composites is not supported. Template path: " + templatePath);
                    }
                    templatesToCompositeClasses.put(templatePath, discoveredCompositeClass);
                }

                List<Class<?>> discoveredAttrConverterClasses =
                        ReflectionUtils.getAnnotatedClasses(formComponentPackage, XMLPropertyGlobalConverter.class);
                for (Class<?> attrConverterClass : discoveredAttrConverterClasses) {
                    if (!IComponentAttributeTypeConverter.class.isAssignableFrom(attrConverterClass)) {
                        throw new RuntimeException(String.format("%s is annotated with %s but does NOT implement %s",
                                attrConverterClass.getName(), XMLPropertyGlobalConverter.class.getSimpleName(),
                                IComponentAttributeTypeConverter.class.getSimpleName()));
                    }
                    Class<?> supportedType = attrConverterClass.getAnnotation(XMLPropertyGlobalConverter.class).value();
                    if (FhLogger.isDebugEnabled(FormReader.class)) {
                        FhLogger.debug(this.getClass(), logger -> logger.log("Discovered attribute converter {} for type {}",
                                attrConverterClass.getName(), supportedType.getName()));
                    }
                    perAttributeTypeConverters.put(supportedType, (IComponentAttributeTypeConverter) attrConverterClass.newInstance());
                }
            }
            jsFiles = new ArrayList<>(allJSSet);
        } catch (Exception exc) {
            FhLogger.error("Problem during initialization", exc);
            throw new FhException(exc);
        }
    }

    @Override
    protected void createNewObject(XmlAttributeReader xmlAttributeReader,
                                   String tagName,
                                   String namespaceURI,
                                   Stack<Component> readObjectsStack,
                                   XMLReaderWorkContext<Form<?>> XMLReaderWorkContext) {

//        if (!"Form".equals(tagName)) for (int i = 0; i < readObjectsStack.size(); i++) System.out.print("   ");
        //System.out.println(tagName);
        if (readObjectsStack.peek() instanceof AvailabilityConfiguration) {
            AvailabilityEnum tag = AvailabilityEnum.getTag(tagName);
            AvailabilityConfiguration availabilityConfiguration = (AvailabilityConfiguration) readObjectsStack.peek();
            switch (tag) {
                case INVISIBLE:
                    availabilityConfiguration.readRulesForInvisible(xmlAttributeReader);
                    break;
                case PREVIEW:
                case READONLY:
                    availabilityConfiguration.readRulesForPreview(xmlAttributeReader);
                    break;
                case EDIT:
                    availabilityConfiguration.readRulesForEdit(xmlAttributeReader);
                    break;
                case SET_BY_PROGRAMMER:
                    availabilityConfiguration.readRulesForSetByProgrammer(xmlAttributeReader);
                    break;
                case VALUE:
                    availabilityConfiguration.readRulesForAvailabilityValue(xmlAttributeReader);
                    break;
                case AVAILABILITY:
                    availabilityConfiguration.readRulesForAvailability(xmlAttributeReader);
                    break;
                case VARIANT:
                    availabilityConfiguration.addVariant(xmlAttributeReader);
                    break;
            }
        } else {
            Class<? extends Component> formComponentElementClass = tagNameToFormComponentClass.get(tagName);
            Component component;
            if (formComponentElementClass != null) {
                Form<?> form = (Form) readObjectsStack.firstElement();
                if (readObjectsStack.size() == 1 && Form.class.isAssignableFrom(formComponentElementClass)) {
                    component = form;
                    migrateModelClass(form, xmlAttributeReader);
                } else {
                    Component parentComponent = readObjectsStack.peek();
                    if(!ComponentServiceImpl.isPossibleToBeAddedForParent(formComponentElementClass, parentComponent.getClass())) {
                        FhLogger.error(formComponentElementClass + " cannot be added for parent of class " + parentComponent.getClass());
                    }

                    try {
                        Optional<Constructor> constructor = ReflectionUtils.findConstructor(formComponentElementClass,
                                Form.class,
                                XmlAttributeReader.class,
                                IGroupingComponent.class,
                                boolean.class);
                        if (constructor.isPresent()) {
                            component = (Component) constructor.get().newInstance(form, xmlAttributeReader, parentComponent, true);
                        } else {
                            constructor = ReflectionUtils.findConstructor(formComponentElementClass,
                                    Form.class);
                            if (!constructor.isPresent()) {
                                throw new RuntimeException("None of supported constructors found in class " + formComponentElementClass.getName());
                            }
                            component = (Component) constructor.get().newInstance(form);
                        }

                        // parent component type may be annotated with MetadataSubelementsParent and child component class may be on the list from this annotation
                        if (isMetadataOnlyElement(parentComponent, component)) {
                            // try to add metadata element to annotated field
                            writeMetadataSubelementsToAnnotatedAttribute(parentComponent, component);
                        } else {
                            // just add as a subcomponent
                            if (parentComponent instanceof IGroupingComponent) {
                                ((IGroupingComponent<Component>) parentComponent).addSubcomponent(component);
                            }
                        }
                        if (parentComponent instanceof IGroupingComponent) {
                            component.setGroupingParentComponent((IGroupingComponent<Component>) parentComponent);
                        }

                        readObjectsStack.add(component);

                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new FhException(e);
                    }
                }

                writeAnnotatedAttributes(form, component, namespaceURI, xmlAttributeReader);
            } else {
                throw new FhException("None of registered classes fits to tag '" + tagName + "'!!");
            }
        }
    }

    protected void migrateModelClass(Form<?> form, XmlAttributeReader xmlAttributeReader) {
        if (form instanceof AdHocForm && xmlAttributeReader.getAttributeValue("modelClass") != null) {
            form.setModelDefinition(new Model(form));
            form.getModelDefinition().setExternalClass(xmlAttributeReader.getAttributeValue("modelClass"));
        }
    }

    @Override
    protected void finalizeNewObjectSetup(String tag, String text, Stack<Component> readObjectsStack, XMLReaderWorkContext XMLReaderWorkContext) {
        Component component = readObjectsStack.peek();
        if (component instanceof AvailabilityConfiguration) {
            if (tagNameToFormComponentClass.containsKey(tag) && tagNameToFormComponentClass.get(tag).equals(AvailabilityConfiguration.class)) {
                ((AvailabilityConfiguration) readObjectsStack.pop()).finalizeReading(text);
            } else {
                ((AvailabilityConfiguration) component).finishReadingOfRules(text);
            }
        } else {
            Component popedComponent = readObjectsStack.pop();
            if (popedComponent instanceof IBodyXml && !StringUtils.isEmpty(text)) {
                // trim whole text
                text = text.trim();
                // check if text needed binding escaping
                if (text.startsWith(ESCAPE_BINDING_TAG_START) && text.endsWith(ESCAPE_BINDING_TAG_END)) {
                    text = text.substring(ESCAPE_BINDING_TAG_START.length(), text.length() - ESCAPE_BINDING_TAG_END.length());
                    text = text.replace("{", "\\{").replace("}", "\\}");
                    // trim inside also
                    text = text.trim();
                }
                ((IBodyXml) popedComponent).setBody(text);
            }
            if (component instanceof FormElement) {
                ((FormElement) popedComponent).finalizeReading(text);
            }
        }
    }

    protected void writeMetadataSubelementsToAnnotatedAttribute(Component parent, Component subelement) {
        Class parentClass = parent.getClass();
        for (Field field : ReflectionUtils.getFieldsWithHierarchy(parent.getClass(), XMLMetadataSubelement.class)) {
            if (field.getType().isAssignableFrom(subelement.getClass())) {
                setValue(parentClass, field, parent, subelement, XMLMetadataSubelement.class, Optional.empty());
                setParentToMetadataSubelement(parent, subelement);
                return;
            }
        }
        for (Field field : ReflectionUtils.getFieldsWithHierarchy(parent.getClass(), XMLMetadataSubelements.class)) {
            if (!Collection.class.isAssignableFrom(field.getType())) {
                throw new RuntimeException(String.format(
                        "Field %s.%s is annotated with %s but is not a collection",
                        field.getDeclaringClass().getName(), field.getName(),
                        XMLMetadataSubelements.class.getSimpleName()));
            }
            Class<?> collectionClass = ReflectionUtils.getGenericTypeInFieldType(field, 0);
            if (collectionClass.isAssignableFrom(subelement.getClass())) {
                Collection collection = (Collection) getValue(parentClass, field, parent, XMLMetadataSubelements.class);
                collection.add(subelement);
                setParentToMetadataSubelement(parent, subelement);
                return;
            }
        }
        throw new RuntimeException(String.format(
                "Trying to set/add metadata element of type %s but none of %s field's are annotated with %s nor %s",
                subelement.getClass().getName(), parentClass.getName(),
                XMLMetadataSubelement.class.getSimpleName(), XMLMetadataSubelements.class.getSimpleName()));
    }

    protected void setParentToMetadataSubelement(Component parent, Component subelement) {
        // set default parent field
        if (parent instanceof IGroupingComponent) {
            subelement.setGroupingParentComponent((IGroupingComponent<?>) parent);
        }

        // set custom fields
        for (Field parentHolderField : ReflectionUtils.getFieldsWithHierarchy(subelement.getClass(), XMLMetadataSubelementParent.class)) {
            if (parentHolderField.getType().isAssignableFrom(parent.getClass())) {
                setValue(subelement.getClass(), parentHolderField, subelement, parent, XMLMetadataSubelementParent.class, Optional.empty());
            }
        }
    }

    protected void writeAnnotatedAttributes(Form<?> form, Component target, String namespaceURI, XmlAttributeReader xmlAttributeReader) {
        Class targetClass = target.getClass();
        for (Field field : ReflectionUtils.getFieldsWithHierarchy(targetClass, XMLProperty.class)) {
            XMLProperty attrAnnotation = field.getAnnotation(XMLProperty.class);
            String attrName = attrAnnotation.value();
            if ("".equals(attrName)) {
                attrName = field.getName();
            }
            String attrValue = xmlAttributeReader.getAttributeValue(attrName);
            if(attrValue == null) {
                attrValue = readAttributeFromAliases(xmlAttributeReader, attrAnnotation.aliases());
            }
            if (attrValue == null && !"".equals(attrAnnotation.defaultValue())) {
                attrValue = attrAnnotation.defaultValue();
            } else if ("xmlns".equals(attrName)) {
                attrValue = namespaceURI;
            }
            if (attrValue != null) {
                Class fieldType = field.getType();

                // map primitives to wrappers
                fieldType = ReflectionUtils.mapPrimitiveToWrapper(fieldType);

                Object fieldValue = convertValue(target, attrValue, field, fieldType);

                setValue(targetClass, field, target, fieldValue, XMLProperty.class, Optional.ofNullable(attrValue));
            }
        }
    }

    private String readAttributeFromAliases(XmlAttributeReader xmlAttributeReader, String[] aliases) {
        for (String alias : aliases) {
            String attributeValue = xmlAttributeReader.getAttributeValue(alias);
            if(attributeValue != null) {
                return attributeValue;
            }
        }
        return null;
    }

    private <T> void setValue(Class<?> targetClass, Field field, Object target, T fieldValue,
                              Class<? extends Annotation> annotation, Optional<String> attrValue) {
        String cacheKey = targetClass.getName() + "." + field.getName();
        if (!fieldSettersCache.containsKey(cacheKey)) {
            fieldSettersCache.put(cacheKey, ReflectionUtils.findSetter(targetClass, field));
        }
        Optional<Method> setter = fieldSettersCache.get(cacheKey);
        if (!setter.isPresent()) {
            throw new RuntimeException(String.format(
                    "Field %s.%s is annotated with %s but doesn't have a public setter",
                    field.getDeclaringClass().getName(), field.getName(),
                    annotation.getSimpleName()));
        }
        try {
            setter.get().invoke(target, fieldValue);
        } catch (Exception e) {
            if (attrValue.isPresent()) {
                throw new RuntimeException(String.format(
                        "Exception while setting value to field %s.%s of type %s created using string '%s'",
                        field.getDeclaringClass().getName(), field.getName(), target.getClass().getName(), attrValue), e);
            } else {
                throw new RuntimeException(String.format(
                        "Exception while setting value to field %s.%s of type %s",
                        field.getDeclaringClass().getName(), field.getName(), target.getClass().getName()), e);
            }
        }
    }

    private <T> T getValue(Class<?> targetClass, Field field, Object target, Class<? extends Annotation> annotation) {
        String cacheKey = targetClass.getName() + "." + field.getName();
        if (!fieldGettersCache.containsKey(cacheKey)) {
            fieldGettersCache.put(cacheKey, ReflectionUtils.findGetter(targetClass, field));
        }
        Optional<Method> getter = fieldGettersCache.get(cacheKey);
        if (!getter.isPresent()) {
            throw new RuntimeException(String.format(
                    "Field %s.%s is annotated with %s but doesn't have a public getter",
                    field.getDeclaringClass().getName(), field.getName(),
                    annotation.getSimpleName()));
        }
        try {
            return (T) getter.get().invoke(target);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Exception while getting value from field %s.%s of type %s",
                    field.getDeclaringClass().getName(), field.getName(), target.getClass().getName()), e);
        }
    }

    public Object convertValue(Component component, String attrValue, Field field, Class fieldType) {
        // first - use converter if present
        Optional<IComponentAttributeTypeConverter<?>> converter = (Optional) getAttributeConverter(component.getClass(), field);
        if (converter.isPresent()) {
            return converter.get().fromXML(component, attrValue);
        }

        if (fieldType == String.class) {
            return attrValue;
        }

        // enums
        if (fieldType.isEnum()) {
            Optional<Enum> enumValue = ReflectionUtils.findEnumConstantIgnoreCase((Class<Enum>) fieldType, attrValue);
            if (enumValue.isPresent()) {
                return enumValue.get();
            } else {
                throw new FhFormException(String.format("Invalid enum %s value in field %s.%s.",
                        fieldType.getSimpleName(), field.getDeclaringClass().getName(), field.getName()));
            }
        }

        // bindings
        if (fieldType == ModelBinding.class) {
            return getComponentForm(component).getComponentBindingCreator().create(component, attrValue, field);
        } else if (fieldType == IndexedModelBinding.class) {
            return getComponentForm(component).createIndexedModelBindingForComponent((IIndexedBindingOwner) component, attrValue, field);
        } else if (fieldType == ActionBinding.class) {
            return getComponentForm(component).createActionBindingForComponent(component, attrValue, field);
        }

        // has string based constructor
        Optional<Constructor<?>> stringBasedConstructor = getStringBasedConstructorConverter(fieldType);
        if (stringBasedConstructor.isPresent()) {
            try {
                return stringBasedConstructor.get().newInstance(attrValue);
            } catch (Exception e) {
                throw new RuntimeException(String.format(
                        "Exception while creating new instace for field %s.%s of type %s using string '%s'",
                        field.getDeclaringClass().getName(), field.getName(), fieldType.getName(), attrValue), e);
            }
        }

        throw new RuntimeException(String.format("Field %s.%s of type %s is annotated with %s but %s is not supported, " +
                        "doesn't have a String based constructor. Please implement %s<%s>.",
                field.getDeclaringClass().getName(), field.getName(), fieldType.getName(),
                XMLProperty.class.getSimpleName(), fieldType.getName(),
                IComponentAttributeTypeConverter.class.getSimpleName(), fieldType.getSimpleName()));
    }

    private Form getComponentForm(Component component) {
        if (component instanceof Form) {
            return (Form) component;
        } else {
            return component.getForm();
        }
    }

    private boolean isMetadataOnlyElement(Component parentComponent, Component component) {
        Class<?> parentClass = parentComponent.getClass();

        // skip runtime compiled classes - go to superclass
        while (!typeMetadataSubelementsCache.containsKey(parentClass)) {
            parentClass = parentClass.getSuperclass();
            if (parentClass == Object.class) {
                return false;
            }
        }

        // get collection of classes from cache and compare with class from the parameter
        for (Class<?> parentsMetadataClass : typeMetadataSubelementsCache.get(parentClass)) {
            if (parentsMetadataClass.isAssignableFrom(component.getClass())) {
                return true;
            }
        }
        return false;
    }

    private void buildMetadataOnlySubelementsCache(Class<?> parentClazz) {
        Set<Class<?>> metadatas = new HashSet<>();
        Class<?> clazz = parentClazz;
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getAnnotation(XMLMetadataSubelement.class) != null) {
                    metadatas.add((Class<Component>) field.getType());
                }
                if (field.getAnnotation(XMLMetadataSubelements.class) != null) {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        metadatas.add(ReflectionUtils.getGenericTypeInFieldType(field, 0));
                    }
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class);
        typeMetadataSubelementsCache.put(parentClazz, metadatas);
    }
}
