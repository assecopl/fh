package pl.fhframework.compiler.forms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.compiler.core.dynamic.dependency.DynamicClassResolver;
import pl.fhframework.compiler.core.generator.DynamicClassCompiler;
import pl.fhframework.compiler.core.generator.EnumsTypeProvider;
import pl.fhframework.compiler.core.generator.ExpressionContext;
import pl.fhframework.compiler.core.generator.RulesTypeProvider;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.rules.service.RulesServiceExt;
import pl.fhframework.Binding;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.annotations.composite.Composite;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.binding.AdHocModelBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.compiler.core.dynamic.*;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhDescribedNstException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GeneratedDynamicClass;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.DebugUtils;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.forms.IFormsUtils;
import pl.fhframework.model.forms.*;
import pl.fhframework.subsystems.ArtificialSubsystem;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;
import pl.fhframework.tools.loading.FormReader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static pl.fhframework.forms.FormsUtils.EXTENDED_SUFIX;

/**
 * Forms manager which creates new form instances from XML or compiled classes. Manages compiled classes' cache.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // must be a singleton
@Primary
public class FormsManager extends AbstractDynamicClassAreaHandler<DynamicFormMetadata> implements IFormsUtils {

    public static final String FORM_ACTIONS_FIELD = "____actions";

    public static final String FORM_VARIANTS_FIELD = "____variants";

    public static final String FORM_INCLUDES_FIELD = "____includes";

    public static final String FORM_INTERNAL_MODEL_CLASS_NAME = "InternalModel";

    public static final Class<?> FORM_INTERNAL_MODEL_TYPE = IInternalModelMarker.class;

    public static final List<String> SIMPLE_FORM_EXTERNAL_MODEL_CLASSES = Arrays.asList(String.class.getName());

    /**
     * Supported (not necessary proposed in GUI) simple form's internal model properties' types
     */
    public static final List<String> SUPPORTED_SIMPLE_FORM_INTERNAL_MODEL_CLASSES =
            Arrays.asList(String.class, Boolean.class, Integer.class, Long.class, Float.class, Double.class,
                    boolean.class, int.class, long.class, float.class, double.class,
                    BigDecimal.class, Date.class, LocalDate.class, LocalDateTime.class, Resource.class)
                    .stream().map(Class::getName).collect(Collectors.toList());

    /**
     * Proposed in GUI simple form's internal model properties' types
     */
    public static final List<String> AVAILABLE_SIMPLE_FORM_INTERNAL_MODEL_CLASSES =
            Arrays.asList(String.class, Boolean.class, Integer.class, Long.class, Float.class, Double.class,
                    BigDecimal.class, Date.class, LocalDate.class, LocalDateTime.class, Resource.class)
                    .stream().map(Class::getName).collect(Collectors.toList());

    public static final String FORM_FILENAME_EXTENSION = "frm";

    private static interface IInternalModelMarker {
    }

    @Value("${fhframework.dynamic.forms.compilationEnabled:true}")
    private boolean compilationEnabled = true;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Autowired
    private IDynamicClassResolver dynamicClassResolver;

    @Autowired
    private DynamicClassCompiler dynamicClassCompiler;

    @Autowired
    private RulesServiceExt rulesService;

    @Autowired
    private EnumsTypeProvider enumsTypeProvider;

    @Override
    public List<Class<?>> listAreaStaticClasses(Subsystem subsystem) {
        return Collections.emptyList();
    }

    public FormsManager() {
        super(FORM_FILENAME_EXTENSION, DynamicClassArea.FORM, false);
    }

    public FormsManager(DynamicClassResolver dynamicClassResolver) {
        this();
        this.dynamicClassResolver = dynamicClassResolver;
    }

    /**
     * Reads form's content from file.
     *
     * @param fullFilePath full file path
     * @param form         form to be read
     */
    public void buildFromFile(FhResource fullFilePath, Form<?> form) {
        buildFromFile(fullFilePath.getURL(), form);
    }

    /**
     * Reads form's content from file.
     *
     * @param fullFileURL full file URL
     * @param form        form to be read
     */
    public void buildFromFile(URL fullFileURL, Form<?> form) {
        String filePath = fullFileURL.toExternalForm();
        long startTime = System.currentTimeMillis();
        FhLogger.info(this.getClass(), "Reading form '{}' from file '{}'...", form.getClass().getName(), filePath);
        FormReader.getInstance().readObject(FhResource.get(fullFileURL), form);
        FhLogger.info(this.getClass(), "Reading form '{}' from file '{}' - done in {} ms", form.getClass().getName(), filePath,
                System.currentTimeMillis() - startTime);
    }


    /**
     * Creates form instance based on form's class.
     *
     * @param formClass form's class
     * @param <M>       type of model
     * @param <F>       type of form
     * @return new form instance
     */
    public <M, F extends Form<M>> F createFormInstance(Class<F> formClass) {
        return createFormInstance(formClass, null, null);
    }

    /**
     * Creates form instance based on form's class.
     *
     * @param formClass form's class
     * @param <M>       type of model
     * @param <F>       type of form
     * @return new form instance
     */
    public <M, F extends Form<M>> F createFormInstance(Class<F> formClass, IComponentBindingCreator bindingCreator, Supplier<Binding> bindingMethods) {
        if (formClass.isAnnotationPresent(GeneratedDynamicClass.class)) {
            return constructNewInstance(formClass, bindingCreator, bindingMethods);
        } else {
            Class<F> extendedClass = (Class<F>) ReflectionUtils.tryGetClassForName(formClass.getName() + EXTENDED_SUFIX);
            // core lite form
            if (extendedClass != null) {
                if (!dynamicClassRepository.isRegisteredDynamicClass(DynamicClassName.forStaticBaseClass(formClass))) {
                    return constructNewInstance(extendedClass, bindingCreator, bindingMethods);
                }
            }

            return createFormInstanceImpl(DynamicClassName.forStaticBaseClass(formClass),
                    Optional.empty(), getFileForFormClass(formClass), formClass, bindingCreator, bindingMethods);
        }
    }

    /**
     * Creates ad hoc form instance based on XML file.
     *
     * @param subsystem    subsystem owning this ad hoc form
     * @param formFilePath file path relative to subsystem base path
     * @param <M>          model class
     * @return new ad hoc form instance
     */
    public <M> AdHocForm<M> createFormInstance(Subsystem subsystem, String formFilePath) {
        return createFormInstanceImpl(DynamicClassName.forXmlFile(formFilePath, FORM_FILENAME_EXTENSION),
                Optional.of(subsystem), formFilePath, AdHocForm.class, null, null);
    }

    /**
     * Lists all forms internal models classes
     *
     * @return all forms internal models classes
     */
    public List<String> getAllFormsInternalModelClassNames() {
        List<String> internalModels = new ArrayList<>();
        for (IClassInfo formClass : dynamicClassRepository.listClasses(DynamicClassFilter.ALL_DYNAMIC_CLASSES, DynamicClassArea.FORM)) {
            try {
                DynamicFormMetadata metadata = dynamicClassRepository.getMetadata(formClass.getClassName());
                if (metadata.isUseInternalModel()) {
                    internalModels.add(formClass.getClassName().toFullClassName() + "$" + FORM_INTERNAL_MODEL_CLASS_NAME);
                }
            } catch (Exception e) {
                FhLogger.error(e);
            }
        }
        return internalModels;
    }

    public <F extends Form> CompilationValidationResult validateFormCompilation(DynamicClassName dynamicClassName,
                                                                                URL fullFileURL,
                                                                                Optional<Predicate<Component>> componentFilter) {
        String generatedClass;
        String tempClassName = dynamicClassName.getBaseClassName() + "_Vtest";
        try {
            DynamicFormMetadata metadata = readMetadata(dynamicClassName, fullFileURL);
            generatedClass = generateClass(metadata, dynamicClassName.getPackageName(), tempClassName,
                    new GenerationContext(), // no timestamps generated
                    dynamicClassRepository.resolveDependencies(metadata.getDependencies()),
                    componentFilter);
        } catch (Throwable e) {
            String componentId = null;
            if (e instanceof FhFormGeneratorException) {
                componentId = FhFormGeneratorException.class.cast(e).getFaultyComponentId();
            }
            StringBuilder message = new StringBuilder();
            Throwable eCause = e;
            while (eCause != null) {
                if (message.length() > 0) {
                    message.append("\n");
                }
                message.append(eCause.getClass().getSimpleName() + ": " + eCause.getMessage());
                eCause = eCause.getCause();
            }
            return new CompilationValidationResult(CompilationValidationResult.Status.GENERATION_ERROR, componentId, message.toString(), DebugUtils.getStackTrace(e));
        }

        Path tempDirectory = null;
        try {
            tempDirectory = Files.createTempDirectory("testFormCompilation");
            Path javaFile = tempDirectory.resolve(tempClassName + ".java");
            Files.write(javaFile, generatedClass.getBytes(Charset.forName("UTF-8")));
            dynamicClassCompiler.compile(Arrays.asList(javaFile), DynamicClassArea.FORM.isAspectWeavingNeeded());
        } catch (Throwable e) {
            return new CompilationValidationResult(CompilationValidationResult.Status.COMPILATION_ERROR, null, e.getMessage(), DebugUtils.getStackTrace(e));
        } finally {
            if (tempDirectory != null) {
                try {
                    Files.walk(tempDirectory, FileVisitOption.FOLLOW_LINKS)
                            .map(Path::toFile)
                            .forEach(File::delete);
                    Files.delete(tempDirectory);
                } catch (IOException e) {
                    FhLogger.error("Cannot delete temporary directory", e);
                }
            }
        }
        return new CompilationValidationResult(CompilationValidationResult.Status.SUCCESS, null, null, null);
    }

    /**
     * Returns declared form's actions signatures
     *
     * @param dynamicClassName class name
     * @return signatures of actions set
     */
    public Set<ActionSignature> getFormActions(DynamicClassName dynamicClassName) {
        Class<?> formClass;
        if (dynamicClassResolver.isRegisteredDynamicClass(dynamicClassName)) {
            formClass = dynamicClassResolver.getOrCompileDynamicClass(dynamicClassName);
        } else {
            formClass = getFormImplClassById(dynamicClassName.toFullClassName());
        }
        return getFormActions((Class<Form<?>>) formClass);
    }

    /**
     * Returns declared form's actions signatures
     *
     * @param formClass real form class
     * @return signatures of actions set
     */
    public Set<ActionSignature> getFormActions(Class<Form<?>> formClass) {
        Field actionsField = org.springframework.util.ReflectionUtils.findField(formClass, FORM_ACTIONS_FIELD);
        if (actionsField == null) {
            throw new FhFormException(formClass.getName() + " class doesn't have form's action list compiled in");
        }
        Set<ActionSignature> actionSignatures = (Set<ActionSignature>) org.springframework.util.ReflectionUtils.getField(actionsField, formClass);
        if (actionSignatures != null) {
            actionSignatures = assignFormId(actionSignatures, DynamicClassName.forClassName(formClass.getName()).toFullClassName());
            Field includesField = org.springframework.util.ReflectionUtils.findField(formClass, FORM_INCLUDES_FIELD);
            if (includesField != null) {
                Set<String> includedForms = (Set<String>) org.springframework.util.ReflectionUtils.getField(includesField, formClass);
                if (includedForms != null) {
                    for (String formId : includedForms) {
                        actionSignatures.addAll(getFormActions(DynamicClassName.forClassName(formId)));
                    }
                }
            }
        }
        return actionSignatures;
    }

    private Set<ActionSignature> assignFormId(Set<ActionSignature> signatures, String formId) {
        return signatures.stream().map(as -> new ActionSignature(as.getActionName(), formId, as.getArgumentTypes())).collect(Collectors.toSet());
    }

    /**
     * Returns declared form's variants
     *
     * @param dynamicClassName class name
     * @return form's variant set
     */
    public Set<String> getFormVariants(DynamicClassName dynamicClassName) {
        Class<?> formClass = dynamicClassResolver.getOrCompileDynamicClass(dynamicClassName);
        Field variantsField = org.springframework.util.ReflectionUtils.findField(formClass, FORM_VARIANTS_FIELD);
        if (variantsField == null) {
            throw new FhFormException(formClass.getName() + " class doesn't have form's variant list compiled in");
        }
        return (Set<String>) org.springframework.util.ReflectionUtils.getField(variantsField, formClass);
    }

    /**
     * Creates a new form instance either a form read from XML or a compiled one.
     * Usage of compiled class and compilation at runtime may be disabled in configuration.
     */
    private <F extends Form<?>> F createFormInstanceImpl(DynamicClassName dynamicClassName,
                                                         Optional<Subsystem> subsystem,
                                                         String formFileRelativePath,
                                                         Class<F> formClass, IComponentBindingCreator bindingCreator, Supplier<Binding> bindingMethods) {
        if (compilationEnabled) {
            // on-demand register out-of-subsystem composite class forms
            if (formClass.isAnnotationPresent(Composite.class) && !dynamicClassRepository.isRegisteredDynamicClass(dynamicClassName)) {
                FhResource subsystemBaseURL = getSubsystemBaseURL(subsystem, formClass);
                ArtificialSubsystem artificialSubsystem = new ArtificialSubsystem("<composite>", FhResource.get(subsystemBaseURL));
                FhResource fullFormPath = subsystemBaseURL.resolve(formFileRelativePath);
                dynamicClassRepository.registerDynamicClassFile(
                        DynamicClassFileDescriptor.forResource(

                                fullFormPath, formFileRelativePath, artificialSubsystem),
                        DynamicClassArea.FORM);
            }
            if (dynamicClassRepository.isRegisteredDynamicClass(dynamicClassName)) {
                try {
                    Class<?> compiledClass = dynamicClassRepository.getOrCompileDynamicClass(dynamicClassName);

                    return constructNewInstance((Class<F>) compiledClass, bindingCreator, bindingMethods);
                } catch (FhDescribedNstException pe) {
                    FhLogger.error("Error in path {}, name {}", formFileRelativePath, dynamicClassName.getBaseClassName());
                    throw pe;
                } catch (FhException pe) {
                    FhLogger.error("Can't find form file, path {}, name {}, check if file exists and is correct", formFileRelativePath, dynamicClassName.getBaseClassName());
                    throw pe;
                }
            } else {
                return constructNewInstance(formClass, bindingCreator, bindingMethods);
            }
        } else {
            URL fullFileURL = resolveFormURL(subsystem, formFileRelativePath, formClass);
            return createFormInstanceFromXML(fullFileURL, formClass, bindingCreator, bindingMethods);
        }
    }

    /**
     * Created a new instance of a provided form class and injects Spring dependencies to it.
     */
    private <F extends Form<?>> F constructNewInstance(Class<F> formClass, IComponentBindingCreator bindingCreator, Supplier<Binding> bindingMethods) {
        try {
            F instance;
            if (applicationContext != null) {
                instance = applicationContext.getAutowireCapableBeanFactory().createBean(formClass);
            } else {
                // useful in maven
                instance = ReflectionUtils.newInstance(formClass);
            }
            if (bindingCreator != null) {
                instance.setComponentBindingCreator(bindingCreator);
            }
            if (bindingMethods != null) {
                instance.setBindingMethodsCreator(bindingMethods);
                instance.setBindingMethods(bindingMethods.get());
            }
            return instance;
        } catch (Throwable e) {
            throw new FhFormException("Error creating new form instance " + formClass.getName(), e);
        }
    }

    /**
     * Creates a new instance of a provided form class, injects Spring dependencies to it and reads controls from XML file.
     */
    private <F extends Form<?>> F createFormInstanceFromXML(URL fullFileURL, Class<F> formClass, IComponentBindingCreator bindingCreator, Supplier<Binding> bindingMethods) {
        F newFormInstance = constructNewInstance(formClass, bindingCreator, bindingMethods);
        buildFromFile(fullFileURL, newFormInstance);
        return newFormInstance;
    }

    /**
     * Gets model type (a simple / generic class) of provided form class.
     */
    public static Type getModelClassForFormClass(Class<? extends Form> formClass) {
        if (AdHocForm.class.isAssignableFrom(formClass)) {
            return null;
        } else {
            return ReflectionUtils.getGenericTypeInSuperclass(formClass, 0);
        }
    }

    /**
     * Resolves full form file URL based on optionally provided subsystem, a relative form file path and a form class.
     * <br>The base URL is taken from (in this order):
     * <ul>
     *     <li>the provided subsystem if present</li>
     *     <li>subsystem which is the owner of a provided form class</li>
     *     <li>base URL </li>
     * </ul>
     *
     * @param subsystem    subsystem (optional)
     * @param formFilePath relavite XML form file's path
     * @param formClass    form class
     * @return full URL
     */
    private URL resolveFormURL(Optional<Subsystem> subsystem, String formFilePath, Class<? extends Form> formClass) {
        return FileUtils.resolve(getSubsystemBaseURL(subsystem, formClass), formFilePath);
    }

    /**
     * Resolves base URL based on optionally provided subsystem, a relative form file path and a form class.
     * <br>The base URL is taken from (in this order):
     * <ul>
     *     <li>the provided subsystem if present</li>
     *     <li>subsystem which is the owner of a provided form class</li>
     *     <li>base URL </li>
     * </ul>
     *
     * @param subsystem subsystem (optional)
     * @param formClass form class
     * @return base URL
     */
    private FhResource getSubsystemBaseURL(Optional<Subsystem> subsystem, Class<? extends Form> formClass) {
        // if subsystem is not explicitly set try to find subsystem which ownes this form class
        if (!subsystem.isPresent()) {
            subsystem = ModuleRegistry.findOwningSubsystem(formClass);
        }

        FhResource baseUrl;
        if (subsystem.isPresent()) {
            baseUrl = subsystem.get().getBasePath();
        } else {
            baseUrl = ReflectionUtils.baseClassPath(formClass);
        }
        return baseUrl;
    }

    @Override
    public DynamicFormMetadata readMetadata(DynamicClassFileDescriptor file) {
        return readMetadata(getDynamicClassName(file.getRelativePath()), file.getResource().getURL());
    }

    private DynamicFormMetadata readMetadata(DynamicClassName className, URL url) {
        DynamicFormMetadata metadata = new DynamicFormMetadata();
        Class<? extends Form> staticFormClass = findFormClass(className);

        Form loadedForm = constructNewInstance(staticFormClass, null, null);
        buildFromFile(url, loadedForm);

        metadata.setDynamicClassName(className);
        metadata.setStaticFormClass(staticFormClass);
        metadata.setDisplayName(loadedForm.getLabel());
        metadata.setLoadedForm(loadedForm);
        calculateModelDependencies(metadata);
        calculateRuleDependencies(metadata);
        calculateFormDependencies(metadata);
        return metadata;
    }

    @Override
    public String generateClass(DynamicFormMetadata metadata,
                                String newClassPackage, String newClassName,
                                GenerationContext xmlTimestampMethod,
                                DependenciesContext dependenciesContext) {
        return generateClass(metadata, newClassPackage, newClassName, xmlTimestampMethod, dependenciesContext, Optional.empty());
    }

    private String generateClass(DynamicFormMetadata metadata,
                                 String newClassPackage, String newClassName,
                                 GenerationContext xmlTimestampMethod,
                                 DependenciesContext dependenciesContext,
                                 Optional<Predicate<Component>> componentFilter) {
        FormsJavaCodeGenerator generator = new FormsJavaCodeGenerator(
                metadata.getLoadedForm(), resolveModelDependency(metadata, dependenciesContext),
                newClassPackage, newClassName, metadata.getDynamicClassName().getBaseClassName(),
                dependenciesContext, xmlTimestampMethod, componentFilter);

//        FormsTypescriptClassGenerator generator2 = new FormsTypescriptClassGenerator(metadata.getLoadedForm());
//        String output2 = generator2.generateClass();
//
//        FormsTypescriptCodeGenerator generator1 = new FormsTypescriptCodeGenerator(metadata.getLoadedForm());
//        String output1 = generator1.generateTemplate();

        return generator.generateClass();
    }

    private Class<? extends Form> findFormClass(DynamicClassName dynamicClassName) {
        try {
            // no need to use custom class loader - we want to load static class
            Class clazz = FhCL.classLoader.loadClass(
                    dynamicClassName.getPackageName() + "." + dynamicClassName.getBaseClassName());
            if (AdHocForm.class.isAssignableFrom(clazz)) {
                return AdHocForm.class;
            }
            return clazz;
        } catch (ClassNotFoundException ignored) {
            return AdHocForm.class;
        }
    }

    /**
     * Deduces dynamic form class name from file path of form file. This name will be a base name of a newly created compiled classes (a file name).
     *
     * @param relativeFilePath relative file path (with / or \)
     * @return dynamic form class name
     */
    public static DynamicClassName getDynamicClassName(String relativeFilePath) {
        DynamicClassName resultClassName = DynamicClassName.forXmlFile(relativeFilePath, FORM_FILENAME_EXTENSION);
        Optional<Class<? extends CompositeForm>> compositeClass = FormReader.getInstance().mapCompositeTemplateClass(relativeFilePath);
        if (compositeClass.isPresent()) {
            resultClassName.setBaseClassName(compositeClass.get().getSimpleName());
        }
        return resultClassName;
    }


    /**
     * Gets a XML file's relative path based on non-ad hoc form class.
     * <br>If provided class is a Composite it may define a different name otherwise the name is the same as a provided class name.
     * Relative direcotry path is taken from the class package.
     */
    private String getFileForFormClass(Class<? extends Form> formClazz) {
        //Get form class annotations
        Composite composite = ReflectionUtils.giveClassAnnotations(formClazz, Composite.class);
        if (composite != null) {
            return formClazz.getPackage().getName().replace(".", File.separator) + File.separator + composite.template();
        } else {
            return formClazz.getCanonicalName().replace(".", File.separator) + ".frm";
        }
    }

    private void calculateModelDependencies(DynamicFormMetadata metadata) {
        Form loadedForm = metadata.getLoadedForm();

        metadata.setUseInternalModel(false);
        if (loadedForm instanceof AdHocForm) {
            // ad-hoc forms specify model class in XML
            if (loadedForm.getModelDefinition() != null) {
                if (loadedForm.getModelDefinition().getExternalClass() != null) {
                    metadata.setModelType(DynamicClassName.forClassName(loadedForm.getModelDefinition().getExternalClass()));
                } else {
                    metadata.setUseInternalModel(true);
                    metadata.setModelType(DynamicClassName.forClassName(metadata.getDynamicClassName().toFullClassName() + "$" + FORM_INTERNAL_MODEL_CLASS_NAME));
                    // internal model dependencies
                    metadata.getDependencies().addAll(FormsManager.getDependencies(loadedForm.getModelDefinition()));
                }
            }
        } else {
            // java class based forms specify model in generic declaration
            Type modelType = getModelClassForFormClass(loadedForm.getClass());
            metadata.setModelFixedType(modelType);
            Class<?> modelClass = ReflectionUtils.getRawClass(modelType);
            // if collection found use element class as dependency instead of collection class
            if (Collection.class.isAssignableFrom(modelClass)) {
                metadata.setModelCollectionClass((Class<? extends Collection<?>>) modelClass);
                Type collectionElementType = ReflectionUtils.getGenericTypeInGenericType(modelType, 0);
                if (collectionElementType != null) {
                    metadata.setModelType(DynamicClassName.forStaticBaseClass(ReflectionUtils.getRawClass(collectionElementType)));
                } else {
                    // collection element type not specified
                    metadata.setModelType(DynamicClassName.forStaticBaseClass(Object.class));
                }
            } else {
                metadata.setModelType(DynamicClassName.forStaticBaseClass(modelClass));
            }
        }
        if (!metadata.isUseInternalModel()) {
            metadata.getDependencies().add(metadata.getModelType());
        }
    }

    private void calculateRuleDependencies(DynamicFormMetadata metadata) {
        // settings' when conditions
        if (metadata.getLoadedForm().getAvailabilityConfiguration() != null) {
            metadata.getLoadedForm().getAvailabilityConfiguration().getSettings().forEach(setting -> calculateRuleDependenciesForSetting(metadata, setting));
        }

        // bindings in components
        calculateRuleDependenciesForComponent(metadata, metadata.getLoadedForm());
        metadata.getLoadedForm().doActionForEverySubcomponentInlcudingRepeated(component -> {
            calculateRuleDependenciesForComponent(metadata, component);
        });
        metadata.getLoadedForm().getNonVisualSubcomponents().forEach(component -> {
            calculateRuleDependenciesForComponent(metadata, component);
        });
    }

    private void calculateFormDependencies(DynamicFormMetadata metadata) {
        metadata.getLoadedForm().doActionForEverySubcomponentInlcudingRepeated(component -> {
            if (component instanceof Includeable) {
                String includedFrm = ((Includeable) component).getStaticRef();
                if (!StringUtils.isNullOrEmpty(includedFrm)) {
                    metadata.getDependencies().add(DynamicClassName.forClassName(includedFrm));
                }
            }
        });
    }

    private void calculateRuleDependenciesForSetting(DynamicFormMetadata metadata, AvailabilityConfiguration.FormSetting setting) {
        if (setting.getWhen() != null) {
            calculateDependenciesForExpression(metadata, setting.getWhen());
        }
        setting.getSubordinateSettings().forEach(subsetting -> calculateRuleDependenciesForSetting(metadata, subsetting));
    }

    private void calculateRuleDependenciesForComponent(DynamicFormMetadata metadata, Component component) {
        org.springframework.util.ReflectionUtils.doWithFields(component.getClass(),
                // find rules in binding expressions
                field -> {
                    field.setAccessible(true);
                    Object fieldValue = field.get(component);
                    if (fieldValue instanceof AdHocModelBinding) {
                        AdHocModelBinding<?> binding = (AdHocModelBinding<?>) fieldValue;
                        if (binding.isCombined()) {
                            for (AdHocModelBinding.CombinedExpression expression : binding.getCombinedExpressions()) {
                                if (expression.isBinding()) {
                                    calculateDependenciesForExpression(metadata, expression.getValue());
                                }
                            }
                        } else if (!binding.isStaticValue()) {
                            calculateDependenciesForExpression(metadata, StringUtils.removeSurroundingBraces(binding.getBindingExpression()));
                        }
                    } else if (fieldValue instanceof ActionBinding) {
                        ActionBinding binding = (ActionBinding) fieldValue;
                        calculateDependenciesForExpression(metadata, binding.getActionBindingExpression());
                    }
                },
                // filter only binding xml attributes
                field -> field.isAnnotationPresent(XMLProperty.class)
                        && (ModelBinding.class.isAssignableFrom(field.getType()) || ActionBinding.class.isAssignableFrom(field.getType()))
        );
    }

    private void calculateDependenciesForExpression(DynamicFormMetadata metadata, String expression) {
        metadata.getDependencies().addAll(rulesService.resolveCalledRules(expression));
        metadata.getDependencies().addAll(enumsTypeProvider.resolveCalledEnums(expression));
    }

    private Type resolveModelDependency(DynamicFormMetadata metadata, DependenciesContext dependenciesContext) {
        if (metadata.isUseInternalModel()) {
            return FORM_INTERNAL_MODEL_TYPE;
        }

        DependencyResolution dependencyResolution = dependenciesContext.resolve(metadata.getModelType());
        if (dependencyResolution.isDynamicClass()) {
            // for dynamic classes using resolved types
            if (!dependencyResolution.isClassReady()) {
                throw new FhFormException("Form generation does not support pending classes resolutions in cycle dependencies with model.");
            }
            // if is a collection wrap with collection generic type
            if (metadata.getModelCollectionClass() != null) {
                return ReflectionUtils.createCollectionType(metadata.getModelCollectionClass(), dependencyResolution.getReadyClass());
            } else {
                return dependencyResolution.getReadyClass();
            }
        } else {
            // for non-dynamic class resolution use declared classes
            Form loadedForm = metadata.getLoadedForm();
            if (loadedForm instanceof AdHocForm) {
                // ad-hoc forms specify model class in XML
                return dependencyResolution.getReadyClass();
            } else {
                // java class based forms specify model in generic declaration
                return metadata.getModelFixedType();
            }
        }
    }

    public static Set<DynamicClassName> getDependencies(Model model) {
        Set<DynamicClassName> dependencies = new HashSet<>();
        for (Property internalProperty : model.getProperties()) {
            if (internalProperty.getType() != null && !FormsManager.SUPPORTED_SIMPLE_FORM_INTERNAL_MODEL_CLASSES.contains(internalProperty.getType())) {
                dependencies.add(DynamicClassName.forClassName(internalProperty.getType()));
            }
        }
        return dependencies;
    }

    @Override
    public Class<? extends Form> getFormById(String formId) {
        if (dynamicClassRepository.isRegisteredDynamicClass(DynamicClassName.forClassName(formId))) {
            return (Class<? extends Form>) dynamicClassRepository.getOrCompileDynamicClass(DynamicClassName.forClassName(formId));
        }
        return (Class<? extends Form>) ReflectionUtils.getClassForName(formId);
    }

    protected Class<? extends Form> getFormImplClassById(String formId) {
        if (dynamicClassResolver.isRegisteredDynamicClass(DynamicClassName.forClassName(formId))) {
            return (Class<? extends Form>) dynamicClassResolver.getOrCompileDynamicClass(DynamicClassName.forClassName(formId));
        }
        Class<? extends Form> formClass = (Class<? extends Form>) ReflectionUtils.getClassForName(formId);
        Class<? extends Form> extendedClass = (Class<? extends Form>) ReflectionUtils.tryGetClassForName(formClass.getName() + EXTENDED_SUFIX);
        // core lite form
        if (extendedClass != null) {
            return extendedClass;
        }

        return formClass;
    }

    public ExpressionContext getBindingContext(Component component, Form<?> form, DynamicFormMetadata metadata, DependenciesContext dependenciesContext) {
        Type modelClass = resolveModelDependency(metadata, dependenciesContext);
        ExpressionContext expressionContext = new ExpressionContext();
        expressionContext.setDefaultBindingRoot("getModel()", modelClass);
        expressionContext.addBindingRoot("THIS", "getModel()", modelClass);
        expressionContext.addBindingRoot("FORM", "getThisForm()", form.getClass());
        expressionContext.addBindingRoot(UserRoleTypeProvider.ROLE_PREFIX, "", UserRoleTypeProvider.ROLE_MARKER_TYPE);
        expressionContext.addBindingRoot(UserPermissionTypeProvider.PERM_PREFIX, "", UserPermissionTypeProvider.PERM_MARKER_TYPE);
        expressionContext.addBindingRoot(RulesTypeProvider.RULE_PREFIX, "__ruleService", DynamicRuleManager.RULE_HINT_TYPE);
        expressionContext.addBindingRoot(EnumsTypeProvider.ENUM_PREFIX, "", DynamicModelManager.ENUM_HINT_TYPE);
        for (LocaleBundle localeBundle : form.getLocaleBundle()) {
            expressionContext.addBindingRoot("$" + localeBundle.getVar(), "__getMessageService().getBundle(\"" + localeBundle.getBasename() + "\")", MessageService.MessageBundle.class);
        }
        expressionContext.addBindingRoot("$", "__getMessageService().getAllBundles()", MessageService.MessageBundle.class);

        return expressionContext;
    }

}
