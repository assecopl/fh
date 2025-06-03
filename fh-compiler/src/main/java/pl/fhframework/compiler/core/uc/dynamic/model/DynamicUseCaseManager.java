package pl.fhframework.compiler.core.uc.dynamic.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.dynamic.AbstractDynamicClassAreaHandler;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassFileDescriptor;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DynamicClassResolver;
import pl.fhframework.compiler.core.model.generator.DynamicModelClassJavaGenerator;
import pl.fhframework.compiler.core.uc.dynamic.generator.DynamicUseCaseCodeBuilder;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Action;
import pl.fhframework.compiler.core.uc.dynamic.model.element.RunUseCase;
import pl.fhframework.compiler.core.uc.dynamic.model.element.StoreAccess;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ActivityTypeEnum;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.CallFunction;
import pl.fhframework.compiler.core.uc.meta.DucExtension;
import pl.fhframework.compiler.core.uc.service.UseCaseService;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.core.services.service.FhServicesService;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.UseCaseBeanFactoryPostProcessor;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.subsystems.Subsystem;

import java.util.*;

/**
 * Forms manager which creates new form instances from XML or compiled classes. Manages compiled
 * classes' cache.
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // must be a singleton
public class DynamicUseCaseManager extends AbstractDynamicClassAreaHandler<DynamicUseCaseMetadata> {

    @Autowired
    private UseCaseService useCaseService;

    @Autowired
    private UseCaseBeanFactoryPostProcessor postProcessor;

    @Autowired
    private RulesService rulesService;

    @Autowired
    private FhServicesService fhServices;

    public DynamicUseCaseManager() {
        super(DucExtension.UC_FILENAME_EXTENSION, DynamicClassArea.USE_CASE, false);
    }

    @Override
    public List<Class<?>> listAreaStaticClasses(Subsystem subsystem) {
        return Collections.emptyList();
    }

    @Override
    public DynamicUseCaseMetadata readMetadata(DynamicClassFileDescriptor file) {
        DynamicUseCaseMetadata metadata = new DynamicUseCaseMetadata();
        DynamicClassName className = DynamicClassName.forXmlFile(file.getRelativePath(), DucExtension.UC_FILENAME_EXTENSION);

        metadata.setDynamicClassName(className);
        metadata.setDynamicUseCase(useCaseService.readUseCase(file.getResource().getURI().getPath()));

        metadata.setDependencies(provideDepenencies(metadata.getDynamicUseCase()));
        return metadata;
    }

    private Set<DynamicClassName> provideDepenencies(UseCase useCase) {
        Set<DynamicClassName> dependencies = new HashSet<>();
        useCase.getParametersAndModel().forEach(parameterDefinition -> {
            if (!isPredefinedType(parameterDefinition.getType())) {
                dependencies.add(DynamicClassName.forClassName(parameterDefinition.getType()));
            }
        });
        useCase.getFlow().getUseCaseElements().forEach(useCaseElement -> {
            if (RunUseCase.class.isInstance(useCaseElement)) {
                dependencies.add(DynamicClassName.forClassName(((RunUseCase)useCaseElement).getRef()));
            }
            else if (Action.class.isInstance(useCaseElement)) {
                Action action = (Action) useCaseElement;
                action.getParameterDefinitions().forEach(parameterDefinition -> {
                    if (!isPredefinedType(parameterDefinition.getType())) {
                        dependencies.add(DynamicClassName.forClassName(parameterDefinition.getType()));
                    }
                });
                action.getCommands().stream().filter(command -> command.getActivityType() == ActivityTypeEnum.DataRead).map(CallFunction.class::cast).forEach(callFunction -> {
                    if (callFunction.getRule() == null) {
                        useCaseService.initDataReadRule(callFunction);
                    }
                    dependencies.add(DynamicClassName.forClassName(UseCaseModelUtils.getDataReadType(callFunction)));
                });
            }
            else if (StoreAccess.class.isInstance(useCaseElement)) {
                StoreAccess storeAccess = StoreAccess.class.cast(useCaseElement);
                dependencies.add(DynamicClassName.forClassName(storeAccess.getSelect()));
            }
        });
        useCase.getUseCaseCache().getCommands().forEach(command -> {
            dependencies.addAll(useCaseService.provideDepenencies(command));
        });
        useCase.getUseCaseCache().getElementsWithParams().forEach(
                withParameters -> dependencies.addAll(useCaseService.provideDepenencies(withParameters)));
        return dependencies;
    }

    private boolean isPredefinedType(String type) {
        return DynamicModelClassJavaGenerator.TYPE_MAPPER.get(type) != null;
    }

    @Override
    public String generateClass(DynamicUseCaseMetadata metadata, String newClassPackage, String newClassName, GenerationContext xmlTimestampMethod, DependenciesContext dependenciesContext) {
        new DynamicClassResolver(dependenciesContext).init();
        DynamicUseCaseCodeBuilder generator = new DynamicUseCaseCodeBuilder();
        generator.initialize(metadata.getDynamicUseCase(), newClassName, newClassPackage, metadata.getDynamicClassName().getBaseClassName(),
                xmlTimestampMethod, dependenciesContext);
        return generator.generateClass();
    }

    @Override
    public void postLoad(DynamicClassFileDescriptor xmlFile, Class<?> clazz, DynamicClassMetadata metadata) {
        super.postLoad( xmlFile, clazz, metadata);

        UseCaseMetadataRegistry.INSTANCE.add((Class<? extends IUseCase>) clazz, xmlFile.getSubsystem());

        Arrays.stream(clazz.getDeclaredClasses()).forEach(innerClass -> {
            try {
                FhCL.classLoader.loadClass(innerClass.getName());
            } catch (ClassNotFoundException e) {
                throw new FhException("Can't load inner class for UseCase", e);
            }
        });
        postProcessor.registerBean(clazz, StringUtils.decapitalize(metadata.getDynamicClassName().getBaseClassName()), true, true);
    }

    @Override
    public void postRegisterDynamicClass(DynamicUseCaseMetadata metadata) {
        if (!StringUtils.isNullOrEmpty(metadata.getDynamicUseCase().getUrl()) &&
                !UseCaseMetadataRegistry.INSTANCE.getByUrlAlias(metadata.getDynamicUseCase().getUrl()).isPresent()) {
            UseCaseMetadataRegistry.INSTANCE.addUrlAlias(useCaseService.getUseCaseInfo(metadata.getDynamicUseCase().getId(), metadata.getDynamicUseCase()));
        }
        super.postRegisterDynamicClass(metadata);
    }
}
