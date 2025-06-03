package pl.fhframework.compiler.core.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.generator.model.Wrapper;
import pl.fhframework.compiler.core.generator.model.data.ClassMm;
import pl.fhframework.compiler.core.generator.model.form.FormMm;
import pl.fhframework.compiler.core.generator.model.rule.RuleMm;
import pl.fhframework.compiler.core.generator.model.service.EndpointMm;
import pl.fhframework.compiler.core.generator.model.service.ServiceMm;
import pl.fhframework.compiler.core.generator.model.usecase.CommandMm;
import pl.fhframework.compiler.core.generator.model.usecase.UseCaseMm;
import pl.fhframework.compiler.core.generator.model.usecase.WithExpression;
import pl.fhframework.compiler.core.model.DynamicModelMetadata;
import pl.fhframework.compiler.core.model.meta.ClassTag;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.services.DynamicServiceMetadata;
import pl.fhframework.compiler.core.uc.dynamic.model.DynamicUseCaseMetadata;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.Command;
import pl.fhframework.compiler.core.uc.service.UseCaseServiceImpl;
import pl.fhframework.compiler.forms.DynamicFormMetadata;
import pl.fhframework.compiler.forms.FormsManager;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCase;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.integration.IEndpointAccess;
import pl.fhframework.integration.IRestUtils;
import pl.fhframework.model.forms.Form;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetaModelService {
    private final DynamicClassRepository classRepository;
    private final UseCaseServiceImpl useCaseService;
    private final FormsManager formsManager;
    private final Optional<IEndpointAccess> endpointAccess;
    private final Optional<IRestUtils> restUtils;

    public ModuleMetaModel provideMetadata(String fullName) {
        DynamicClassName artifactName = DynamicClassName.forClassName(fullName);
        if (classRepository.isRegisteredDynamicClass(artifactName)) {
            Set<IClassInfo> allArtifacts = new HashSet<>();
            IClassInfo artefact = classRepository.getInfo(artifactName);
            fillArtifacts(artefact, artefact.getSubsystem(), allArtifacts);

            ModuleMetaModel moduleMetaModel = getModuleMetaModel(allArtifacts, artefact.getSubsystem());
            if (artefact.getArea() == DynamicClassArea.USE_CASE) {
                moduleMetaModel.setInitialPrimaryUc(fullName);
            }
            return moduleMetaModel;
        }
        throw new FhException(String.format("Unknown artifact: %s", fullName));
    }

    public ModuleMetaModel provideModuleMetadata(String moduleName) {
        Set<IClassInfo> allArtifacts = classRepository.getRegistered(moduleName);
        Subsystem subsystem = ModuleRegistry.getByName(moduleName);

        return getModuleMetaModel(allArtifacts, subsystem);
    }

    protected ModuleMetaModel getModuleMetaModel(Set<IClassInfo> allArtifacts, Subsystem subsystem) {
        ModuleMetaModel generationData = new ModuleMetaModel(subsystem);

        Set<DynamicClassName> allDependencies = new HashSet<>();
        allArtifacts.forEach(info -> fillMetadata(info, generationData, allDependencies));
        fillExternalDependencies(generationData, allArtifacts, allDependencies);
        fillEndpoints(generationData);
        return generationData;
    }

    private void fillEndpoints(ModuleMetaModel generationData) {
        if (endpointAccess.isPresent() && restUtils.isPresent()) {
            List<String> endpointsName = endpointAccess.get().findAllNames();
            endpointsName.forEach(name -> {
                generationData.getEndpoints().add(new EndpointMm(name, restUtils.get().buildRestResource(name, null, "").getUrl()));
            });
        }
    }

    public ExpressionContext provideContext(WithExpression element, Wrapper wrapper) {
        if (wrapper instanceof UseCaseMm) {
            return useCaseService.getBindingContext(element.provideImpl(), useCaseService.getUseCaseContext(wrapper.provideImpl()));
        }
        if (wrapper instanceof FormMm) {
            // todo: inefficient and using Type class (should not depend on java Type class)
            DynamicClassName dcn = DynamicClassName.forClassName(((FormMm) wrapper).getId());
            return formsManager.getBindingContext(element.provideImpl(), wrapper.provideImpl(),
                    classRepository.getMetadata(dcn),
                    classRepository.resolveDependencies(Collections.singleton(dcn)));
        }
        throw new FhException("Unknown root metamodel");
    }

    public DependenciesContext provideDependeciesContext(String id) {
        DynamicClassName dcn = DynamicClassName.forClassName(id);
        return classRepository.resolveDependencies(Collections.singleton(dcn));
    }

    /**
     * Returns called Rules and Services
     * @param command         command
     * @return called Rules and Services
     */
    public Set<DynamicClassName> findCallers(CommandMm<?> command) {
        return useCaseService.provideDepenencies((Command) command.provideImpl());
    }

    /**
     * Returns called Rules and Services
     * @param expression  fhel expression
     * @return called Rules and Services
     */
    public Set<DynamicClassName> findCallers(ExpressionMm expression) {
        return useCaseService.provideDepenencies(expression);
    }


    private void fillMetadata(IClassInfo info, ModuleMetaModel generationData, Set<DynamicClassName> allDependencies) {
        if (info.isDynamic()) {
            DynamicClassMetadata classMetadata = classRepository.getMetadata(info.getClassName());
            if (classMetadata instanceof DynamicModelMetadata) {
                if (((DynamicModelMetadata) classMetadata).getDynamicClass().isXsdSchema()) {
                    // todo: xsd support
                    FhLogger.warn(String.format("Xsd generated model is not supported %s", classMetadata.getDynamicClassName().toFullClassName()));
                    return;
                }
            }
            generationData.addMetadata(info.getClassName().toFullClassName(),
                    convertToMm(info.getClassName().toFullClassName(), retrieveArtifact(classMetadata), mapDependencies(classMetadata.getDependencies()), generationData));
            allDependencies.addAll(classMetadata.getDependencies());
        }
        else {
            fillStaticDependency(generationData, info);
        }
    }

    private List<String> mapDependencies(Set<DynamicClassName> dependencies) {
        return dependencies.stream().map(DynamicClassName::toFullClassName).filter(name -> !name.startsWith("java.")).collect(Collectors.toList());
    }

    private Object convertToMm(String fullName, Object metadata, List<String> dependencies, ModuleMetaModel generationData) {
        if (metadata instanceof ClassTag) {
            return new ClassMm((ClassTag) metadata, dependencies);
        }
        else if (metadata instanceof Rule) {
            return new RuleMm((Rule) metadata, dependencies, generationData);
        }
        else if (metadata instanceof pl.fhframework.compiler.core.services.dynamic.model.Service) {
            return new ServiceMm((pl.fhframework.compiler.core.services.dynamic.model.Service) metadata, dependencies, generationData);
        }
        else if (metadata instanceof Form) {
            ((Form<?>) metadata).setGenerationUtils(new GenerationUtils.Form((Form<?>) metadata, fullName, generationData));
            return  new FormMm((Form<?>) metadata, dependencies, generationData, fullName);
        }
        else if (metadata instanceof UseCase) {
            return new UseCaseMm((UseCase) metadata, dependencies, generationData);
        }
        else {
            throw new IllegalArgumentException("Unknown metadata type");
        }
    }

    private void fillExternalDependencies(ModuleMetaModel generationData, Set<IClassInfo> allArtifacts, Set<DynamicClassName> allDependencies) {
        allArtifacts.forEach(artifact -> allDependencies.remove(artifact.getClassName()));

        allDependencies.stream().filter(className -> !className.getInnerClassName().isPresent()).forEach(dep -> {
            if (generationData.getMetadata(dep.toFullClassName()) == null) { // not from the same module
                if (classRepository.isRegisteredStaticClass(dep)) {
                    fillStaticDependency(generationData, classRepository.getInfo(dep));
                }
                else if (classRepository.isRegisteredDynamicClass(dep)) {
                    fillDynamicDependency(generationData, dep.getOuterClassName());
                }
                else {
                    // static unregistered (e.g. uc)
                    // todo: type
                    generationData.addExternalDependency(Dependency.builder().
                            name(dep.toFullClassName()).
                            provided(true).
                            module(ModuleRegistry.getByName(ModuleRegistry.getModuleId(dep))).
                            core(dep.toFullClassName().startsWith("pl.fhframework")).build());
                }
            }
        });
    }

    private void fillDynamicDependency(ModuleMetaModel generationData, DynamicClassName dep) {
        IClassInfo info = classRepository.getInfo(dep);
        DynamicClassMetadata dcm = classRepository.getMetadata(dep);
        generationData.addExternalDependency(Dependency.builder().
                name(dep.toFullClassName()).
                metadata(convertToMm(dep.toFullClassName(), retrieveArtifact(dcm), mapDependencies(dcm.getDependencies()), generationData)).
                module(info.getSubsystem()).
                type(info.getArea()).build());
    }

    private void fillStaticDependency(ModuleMetaModel generationData, IClassInfo info) {
        generationData.addExternalDependency(Dependency.builder().
                name(info.getClassName().toFullClassName()).
                provided(true).
                staticClass(classRepository.getStaticClass(info.getClassName())).
                module(info.getSubsystem()).
                type(info.getArea()).
                core(info.getClassName().getPackageName().startsWith("pl.fhframework")).build());
    }

    private void fillArtifacts(IClassInfo artefact, Subsystem subsystem, Set<IClassInfo> allArtifacts) {
        allArtifacts.add(artefact);
        Set<DynamicClassName> dependencies = classRepository.getMetadata(artefact.getClassName()).getDependencies();
        dependencies.forEach(dep -> {
            if (classRepository.isRegisteredDynamicClass(dep.getOuterClassName()) ||
             classRepository.isRegisteredStaticClass(dep.getOuterClassName())) {
                IClassInfo info = classRepository.getInfo(dep.getOuterClassName());
                if (info.getSubsystem() == subsystem && !allArtifacts.contains(info)) {
                    fillArtifacts(info, subsystem, allArtifacts);
                }
            }
            else {
                // todo: ? create IClassInfo for static class
            }
        });
    }

    private <T> T retrieveArtifact(DynamicClassMetadata classMetadata) {
        if (classMetadata instanceof DynamicModelMetadata) {
            return (T) ((DynamicModelMetadata) classMetadata).getDynamicClass();
        }
        if (classMetadata instanceof DynamicRuleMetadata) {
            return (T) ((DynamicRuleMetadata) classMetadata).getRule();
        }
        if (classMetadata instanceof DynamicServiceMetadata) {
            return (T) ((DynamicServiceMetadata) classMetadata).getService();
        }
        if (classMetadata instanceof DynamicFormMetadata) {
            return (T) ((DynamicFormMetadata) classMetadata).getLoadedForm();
        }
        if (classMetadata instanceof DynamicUseCaseMetadata) {
            return (T) ((DynamicUseCaseMetadata) classMetadata).getDynamicUseCase();
        }
        throw new IllegalArgumentException("Unknown metadata type");
    }

    private boolean sameSubsystem(Subsystem sub1, Subsystem sub2) {
        return Objects.equals(sub1.getName(), sub2.getName());
    }
}
