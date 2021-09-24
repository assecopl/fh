package pl.fhframework.subsystems;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhFrameworkException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.events.ISubsystemLifecycleListener;
import pl.fhframework.core.generator.GeneratedDynamicClass;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.core.model.Hidden;
import pl.fhframework.core.model.meta.ModelMetadataRegistry;
import pl.fhframework.core.rules.AccessibilityRule;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.rules.FilteringRule;
import pl.fhframework.core.rules.ValidationRule;
import pl.fhframework.core.rules.meta.RuleMetadataRegistry;
import pl.fhframework.core.services.FhService;
import pl.fhframework.core.services.meta.DefaultServiceLocator;
import pl.fhframework.core.services.meta.ServiceMetadataRegistry;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.IDescribableService;
import pl.fhframework.modules.services.ServiceHandle;
import pl.fhframework.modules.services.ServiceDescriptor;
import pl.fhframework.ReflectionUtils;

import javax.persistence.Entity;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // important
@Order(1)
public class ModuleRegistry implements ApplicationListener<ContextRefreshedEvent> {
    /**
     * Set of URLs of FH modules present on the application classpath
     */
    private static final Map<String, Subsystem> FH_MODULES_ON_CLASSPATH = new HashMap<>();
    private static final Map<String, Subsystem> FH_MODULES_BASE_PACKAGE = new HashMap<>();
    private static final Map<String, String> FH_MODULES_PRODUCT_LABEL = new ConcurrentHashMap<>(); // Product UUID to Product Label
    private static final Map<FhResource, Subsystem> FH_MODULES_ON_CLASSPATH_BY_CLASSPATH_URL = new HashMap<>();
    private static final Set<Subsystem> LOADED_MODULES = new LinkedHashSet<>();
    private static final String MODULE_CONFIGURATION_FILE_PATH = "module.sys";

    @Autowired
    private DefaultServiceLocator describableServiceRegistry;

    static {
        ClassLoader classLoader = FhCL.classLoader;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            List<Resource> resources = new ArrayList<>();
            resources.addAll(Arrays.asList(resolver.getResources(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + MODULE_CONFIGURATION_FILE_PATH)));
            for (Resource moduleResource : resources) {
                Subsystem subsystem = DynamicSubsystemReader.instance.readSubsystemConfiguration(
                        FhResource.get(moduleResource));// there is also something like DynamicTreeElementsReader
                if (subsystem != null) {
                    FH_MODULES_ON_CLASSPATH.put(subsystem.getName(), subsystem);
                    FH_MODULES_BASE_PACKAGE.put(subsystem.getBasePackage(), subsystem);
                    FH_MODULES_ON_CLASSPATH_BY_CLASSPATH_URL.put(subsystem.getBaseClassPath(), subsystem);
                    FH_MODULES_PRODUCT_LABEL.putIfAbsent(subsystem.getProductUUID(), subsystem.getProductLabel());
                }
            }
        } catch (IOException exception) {
            FhLogger.error("Cannot load module", exception);
            throw new FhFrameworkException(exception);
        }
    }

    public static ModuleRegistryBuilder registry() {
        return new ModuleRegistryBuilder();
    }

    /**
     * @param moduleName module name
     * @deprecated Use {@link #registry()}.
     */
    @Deprecated
    public static void loadModule(String moduleName) {
        if (isModuleLoaded(moduleName)) {
            return;
        }
        Subsystem subsystem = FH_MODULES_ON_CLASSPATH.get(moduleName);
        if (subsystem == null) {
            throw new FhFrameworkException("Module not available on the classpath: " + moduleName);
        }
        loadModuleStaticEntities(subsystem);
        loadModuleStaticServices(subsystem);
        loadModuleUseCases(subsystem);
        loadModuleRules(subsystem);
        loadDependentModules(subsystem.getDependentModulesNames());
        LOADED_MODULES.add(subsystem);
    }

    private static void loadModuleStaticEntities(Subsystem subsystem) {
        List<Class<?>> entityClasses = ReflectionUtils.getAnnotatedClasses(subsystem.getBasePackage(), Entity.class);

        List<Class<? extends BaseEntity>> staticEntityClasses = entityClasses.stream().
                filter(BaseEntity.class::isAssignableFrom).
                filter(aClass -> aClass.getAnnotation(Hidden.class) == null).
                map(aClass -> (Class<? extends BaseEntity>) aClass).collect(Collectors.toList());

        staticEntityClasses.forEach(aClass -> ModelMetadataRegistry.INSTANCE.addStaticEntity(aClass, subsystem));
    }

    private static void loadModuleStaticServices(Subsystem subsystem) {
        List<Class<?>> serviceClasses = ReflectionUtils.getAnnotatedClasses(subsystem.getBasePackage(), FhService.class);

        serviceClasses.stream().filter(aClass -> aClass.getAnnotation(GeneratedDynamicClass.class) == null).forEach(aClass -> ServiceMetadataRegistry.INSTANCE.addStaticService(aClass, subsystem, aClass.getAnnotation(FhService.class).categories()));
    }

    public static Subsystem getByName(String name) {
        return FH_MODULES_ON_CLASSPATH.get(name);
    }

    public static String getModuleProductLabel(String productUUID) {
        return FH_MODULES_PRODUCT_LABEL.get(productUUID);
    }

    static Set<String> getFhModulesNameOnClasspath() {
        return new LinkedHashSet<>(FH_MODULES_ON_CLASSPATH.keySet());
    }

    /**
     * Finds a subsystem which ownes passed class, e.g. finds subsystem owning particular form class.
     *
     * @param subsystemOwnedClass a class
     * @return owning subsystem (optional)
     */
    public static Optional<Subsystem> findOwningSubsystem(Class<?> subsystemOwnedClass) {
        FhResource fhResource = ReflectionUtils.baseClassPath(subsystemOwnedClass);
        return Optional.ofNullable(FH_MODULES_ON_CLASSPATH_BY_CLASSPATH_URL.get(fhResource));
    }

    /**
     * Finds a subsystem which ownes passed class by external path
     *
     * @param subsystemOwnedClass a class
     * @return owning subsystem (optional)
     */
    public static Optional<Subsystem> findOwningSubsystemByExternalPath(Class<?> subsystemOwnedClass) {
        FhResource fhResource = ReflectionUtils.baseClassPath(subsystemOwnedClass);
        return FH_MODULES_ON_CLASSPATH_BY_CLASSPATH_URL.entrySet()
                .stream()
                .filter(c -> c.getKey().getExternalPath().equals(fhResource.getExternalPath()))
                .map(Map.Entry::getValue).findAny();
    }

    private static boolean isModuleLoaded(String moduleName) {
        for (Subsystem loadedSubsystem : LOADED_MODULES) {
            if (loadedSubsystem.getName().equals(moduleName)) {
                return true;
            }
        }
        return false;
    }

    private static void loadDependentModules(Set<String> dependentModulesNames) {
        for (String dependentSubsystem : dependentModulesNames) {
            loadModule(dependentSubsystem);
        }
    }

    private static void loadModuleUseCases(Subsystem subsystem) {
        ClassPathScanningCandidateComponentProvider classPathScanner = new ClassPathScanningCandidateComponentProvider(false);
        classPathScanner.addExcludeFilter(new AnnotationTypeFilter(GeneratedDynamicClass.class));
        classPathScanner.addIncludeFilter(new AnnotationTypeFilter(UseCase.class));
        for (BeanDefinition beanDefinition : classPathScanner.findCandidateComponents(subsystem.getBasePackage())) {
            AbstractBeanDefinition abstractBeanDefinition = (AbstractBeanDefinition) beanDefinition;
            try {
                if (abstractBeanDefinition.getResource().getURL().toExternalForm().startsWith(subsystem.getBaseClassPath().getURL().toExternalForm())) {
                    Class<? extends IUseCase> useCaseClazz = (Class<? extends IUseCase>) FhCL.classLoader.loadClass(abstractBeanDefinition.getBeanClassName());
                    subsystem.addUseCase(useCaseClazz);
                    UseCaseMetadataRegistry.INSTANCE.add(useCaseClazz, subsystem);
                }
            } catch (IOException | ClassNotFoundException exception) {
                FhLogger.error("Cannot load use case configuration for {} module", subsystem, exception);
                throw new FhFrameworkException(exception);
            }
        }
    }

    private static void loadModuleRules(Subsystem subsystem) {
        ClassPathScanningCandidateComponentProvider classPathScanner = new ClassPathScanningCandidateComponentProvider(false);
        classPathScanner.addExcludeFilter(new AnnotationTypeFilter(GeneratedDynamicClass.class));
        classPathScanner.addIncludeFilter(new AnnotationTypeFilter(ValidationRule.class));
        classPathScanner.addIncludeFilter(new AnnotationTypeFilter(AccessibilityRule.class));
        classPathScanner.addIncludeFilter(new AnnotationTypeFilter(BusinessRule.class));
        classPathScanner.addIncludeFilter(new AnnotationTypeFilter(FilteringRule.class));

        for (BeanDefinition beanDefinition : classPathScanner.findCandidateComponents(subsystem.getBasePackage())) {
            AbstractBeanDefinition abstractBeanDefinition = (AbstractBeanDefinition) beanDefinition;
            try {
                if (abstractBeanDefinition.getResource().getURL().toExternalForm().startsWith(subsystem.getBaseClassPath().getURL().toExternalForm())) {
                    Class<?> clazz = FhCL.classLoader.loadClass(abstractBeanDefinition.getBeanClassName());
                    if (clazz.getAnnotation(AccessibilityRule.class) != null) {
                        RuleMetadataRegistry.INSTANCE.addAccessibilityRule(clazz, subsystem, clazz.getAnnotation(AccessibilityRule.class).categories());
                    } else if (clazz.getAnnotation(ValidationRule.class) != null) {
                        RuleMetadataRegistry.INSTANCE.addValidationRule(clazz, subsystem, clazz.getAnnotation(ValidationRule.class).categories());
                    } else if (clazz.getAnnotation(BusinessRule.class) != null) {
                        RuleMetadataRegistry.INSTANCE.addBusinessRule(clazz, subsystem, clazz.getAnnotation(BusinessRule.class).categories());
                    } else if (clazz.getAnnotation(FilteringRule.class) != null) {
                        RuleMetadataRegistry.INSTANCE.addFilteringRule(clazz, subsystem, clazz.getAnnotation(FilteringRule.class).categories());
                    }
                }
            } catch (IOException | ClassNotFoundException exception) {
                FhLogger.error("Cannot load rules configuration for {} module", subsystem, exception);
                throw new FhFrameworkException(exception);
            }
        }
    }

    public static Subsystem getUseCaseSubsystem(String fullyQualifiedUseCaseClassName) {
        //TODO: check whether two modules contain use case with the same name
        for (Subsystem subsystem : LOADED_MODULES) {
            if (subsystem.containsUseCase(fullyQualifiedUseCaseClassName)) {
                return subsystem;
            }
        }
        throw new FhFrameworkException("There is no module that contains given use case: " + fullyQualifiedUseCaseClassName);
    }

    public static Set<Subsystem> getLoadedModules() {
        return Collections.unmodifiableSet(LOADED_MODULES);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        AutowireCapableBeanFactory autowireFactory = event.getApplicationContext().getAutowireCapableBeanFactory();
        for (Subsystem loadedSubsystem : getLoadedModules()) {
            if (loadedSubsystem instanceof DynamicSubsystem) {
                for (Class<? extends ISubsystemLifecycleListener> listener : DynamicSubsystem.class.cast(loadedSubsystem).getLifecycleListeners()) {
                    try {
                        autowireFactory.createBean(listener).onSubsystemStart(loadedSubsystem);
                    } catch (Throwable e) {
                        FhLogger.error("Exception while running on start listener {} of subsystem {}", listener.getName(), loadedSubsystem.getName(), e);
                    }
                }
            }
        }
        UseCaseMetadataRegistry.INSTANCE.getAll().forEach(useCaseInfo -> {
            if (useCaseInfo.getClazz() != null && !useCaseInfo.getImplementedInterfaces().isEmpty() && IDescribableService.class.isAssignableFrom(useCaseInfo.getClazz())) {
                fillServiceClass(useCaseInfo.getClazz(), useCaseInfo.getId(), (List) useCaseInfo.getImplementedInterfaces(), event.getApplicationContext());
            }
        });

        ServiceMetadataRegistry.INSTANCE.getStaticServices().forEach(serviceClass -> {
            if (IDescribableService.class.isAssignableFrom(serviceClass) && serviceClass.getInterfaces().length > 0) {
                fillServiceClass(serviceClass, serviceClass.getName(), Arrays.asList(serviceClass.getInterfaces()), event.getApplicationContext());
            }
        });
    }

    private void fillServiceClass(Class<?> clazz, String className, List<Class> classInterfaces, ApplicationContext applicationContext) {
        ServiceDescriptor serviceDescriptor = clazz.getAnnotation(ServiceDescriptor.class);
        if (serviceDescriptor != null) {
            Object descriptor;
            if (serviceDescriptor.bean() != void.class) {
                descriptor = applicationContext.getBean(serviceDescriptor.bean());
            }
            else {
                descriptor = applicationContext.getBean(StringUtils.nvl(serviceDescriptor.name(), serviceDescriptor.value()));
            }
            classInterfaces.forEach(classInterface -> {
                if (IDescribableService.class.isAssignableFrom(classInterface)) {
                    describableServiceRegistry.addService(ServiceHandle.builder().
                            serviceClassName(className).
                            serviceInterface(classInterface).
                            serviceDescriptor(descriptor).build());
                }
            });
        }
    }

    public static String getModuleId(Class clazz) {
        if (clazz != null) {
            DynamicClassName dynamicClassName = DynamicClassName.forClassName(ReflectionUtils.getClassName(clazz));

            return getModuleId(dynamicClassName);
        }

        return null;
    }

    public static String getModuleId(DynamicClassName dynamicClassName) {
        if (dynamicClassName != null) {
            Subsystem subsystem;
            do {
                subsystem = FH_MODULES_BASE_PACKAGE.get(dynamicClassName.getPackageName());
                dynamicClassName = DynamicClassName.forClassName(dynamicClassName.getPackageName());
            } while (subsystem == null && !StringUtils.isNullOrEmpty(dynamicClassName.getPackageName()));
            if (subsystem != null) {
                return subsystem.getName();
            }
        }

        return null;
    }

    public static List<String> getModulesBasePackages() {
        return new ArrayList<>(FH_MODULES_BASE_PACKAGE.keySet());
    }
}
