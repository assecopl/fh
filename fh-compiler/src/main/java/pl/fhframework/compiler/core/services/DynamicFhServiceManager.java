package pl.fhframework.compiler.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import pl.fhframework.compiler.core.dynamic.*;
import pl.fhframework.compiler.core.generator.FhServicesTypeProvider;
import pl.fhframework.compiler.core.generator.HolderType;
import pl.fhframework.compiler.core.generator.ICollapsePropertiesToMethodName;
import pl.fhframework.compiler.core.services.dynamic.generator.FhServiceCodeBuilder;
import pl.fhframework.compiler.core.services.dynamic.model.Service;
import pl.fhframework.compiler.core.services.meta.SrvExtension;
import pl.fhframework.compiler.core.services.service.FhServicesServiceExtImpl;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.services.meta.ServiceMetadataRegistry;
import pl.fhframework.core.uc.UseCaseBeanFactoryPostProcessor;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.ServiceTypeEnum;
import pl.fhframework.subsystems.Subsystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pawel.ruta on 2017-11-30.
 *
 * Bean that can be used later to dynamically change exposed Fh Services
 */
@org.springframework.stereotype.Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // must be a singleton
public class DynamicFhServiceManager extends AbstractDynamicClassAreaHandler<DynamicServiceMetadata> {
    public static final Class<?> SERVICE_HINT_TYPE = IServiceHintType.class;
    public static final String SERVICE_HINT_TYPE_NAME = "Services...";
    public static final String SERVICE_NAME = "__fhService";

    private final Map<DynamicClassName, DynamicServiceMetadata> restServicesToRegister = new ConcurrentHashMap<>();

    private interface IServiceHintType<E extends HolderType> extends ICollapsePropertiesToMethodName {}

    @Autowired
    private FhServicesServiceExtImpl fhServices;

    @Autowired
    private UseCaseBeanFactoryPostProcessor postProcessor;

    @Autowired
    private FhServicesTypeProvider servicesTypeProvider;

    public DynamicFhServiceManager() {
        super(SrvExtension.SERVICE_FILENAME_EXTENSION, DynamicClassArea.SERVICE, false);;
    }

    @Override
    public List<Class<?>> listAreaStaticClasses(Subsystem subsystem) {
        List<Class<?>> staticClasses = new LinkedList<>();

        staticClasses.addAll(ServiceMetadataRegistry.INSTANCE.getStaticServices(subsystem));

        return staticClasses;
    }

    @Override
    public DynamicServiceMetadata readMetadata(DynamicClassFileDescriptor file) {
        DynamicServiceMetadata metadata = new DynamicServiceMetadata();
        DynamicClassName className = DynamicClassName.forXmlFile(file.getRelativePath(), SrvExtension.SERVICE_FILENAME_EXTENSION);
        try {
            metadata.setService(fhServices.readService(file.getResource().getURL()));
            metadata.setDisplayName(metadata.getService().getLabel());
            metadata.setDependencies(provideDependencies(metadata.getService()));
        } catch (Exception e) {
            FhLogger.error(String.format("Error reading metada of '%s'", className.toFullClassName()), e);
            metadata.setService(null);
        }
        metadata.setDynamicClassName(className);

        return metadata;
    }

    @Override
    public String generateClass(DynamicServiceMetadata metadata, String newClassPackage, String newClassName, GenerationContext xmlTimestampMethod, DependenciesContext dependenciesContext) {
        FhServiceCodeBuilder generator = new FhServiceCodeBuilder();
        generator.initialize(metadata.getService(), newClassName, newClassPackage, metadata.getDynamicClassName().getBaseClassName(),
                xmlTimestampMethod, dependenciesContext);

        return generator.generateClass();
    }

    private Set<DynamicClassName> provideDependencies(Service service) {
        return  fhServices.provideDependencies(service);
    }

    @Override
    public void postLoad(DynamicClassFileDescriptor xmlFile, Class<?> clazz, DynamicClassMetadata metadata) {
        super.postLoad( xmlFile, clazz, metadata);

        postProcessor.registerBean(clazz, StringUtils.decapitalize(metadata.getDynamicClassName().getBaseClassName()), true, true);

        ServiceMetadataRegistry.INSTANCE.register(clazz, xmlFile.getSubsystem());
        DynamicServiceMetadata serviceMetadata = (DynamicServiceMetadata) metadata;
        if (serviceMetadata.getService().getServiceType() == ServiceTypeEnum.RestService) {
            restServicesToRegister.remove(metadata.getDynamicClassName());
            fhServices.registerRestService(clazz, serviceMetadata);
        }
        ServiceMetadataRegistry.INSTANCE.addCategories(((DynamicServiceMetadata) metadata).getService().getCategories());
    }

    @Override
    public void postRegisterDynamicClass(DynamicServiceMetadata metadata) {
        ServiceMetadataRegistry.INSTANCE.addCategories(metadata.getService().getCategories());
        servicesTypeProvider.refresh();
        if (metadata.getService().getServiceType() == ServiceTypeEnum.RestService) {
            restServicesToRegister.put(metadata.getDynamicClassName(), metadata);
        }
    }

    @Override
    public void postUnregisterDynamicClass(DynamicServiceMetadata metadata) {
        servicesTypeProvider.refresh();

        if (metadata.getService().getServiceType() == ServiceTypeEnum.RestService) {
            fhServices.unregisterRestService(metadata);
        }
    }

    @Override
    public void preUpdateDynamicClass(DynamicServiceMetadata metadata) {
        if (metadata.getService() != null && metadata.getService().getServiceType() == ServiceTypeEnum.RestService) {
            fhServices.unregisterRestService(metadata);
        }
    }

    @Override
    public void postUpdateDynamicClass(DynamicServiceMetadata metadata) {
        ServiceMetadataRegistry.INSTANCE.addCategories(metadata.getService().getCategories());
        servicesTypeProvider.refresh();
        if (metadata.getService().getServiceType() == ServiceTypeEnum.RestService) {
            fhServices.registerRestService(null, metadata);
        }
    }

    @Override
    public void postAllLoad(IDynamicClassResolver dynamicClassResolver) {
        for (DynamicClassName dynamicClassName : new HashSet<>(restServicesToRegister.keySet())) {
            DynamicServiceMetadata dynamicServiceMetadata = restServicesToRegister.remove(dynamicClassName);
            if (dynamicServiceMetadata != null) {
                try {
                    dynamicClassResolver.getOrCompileDynamicClass(dynamicClassName);
                } catch (Exception e) {
                    FhLogger.error(e);
                }
            }
        }
    }
}
