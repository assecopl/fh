package pl.fhframework.compiler.core.generator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/metamodel")
@RequiredArgsConstructor
public class MetaModelRestService {
    private final MetaModelService metaModelService;
    private final DynamicClassRepository classRepository;

    /**
     * Provides metamodel for specified artefact and its dependencies.
     *
     * @param fullQName fully qualified name of artefact (id)
     * @return metamodel of artefact and dependencies
     */
    @GetMapping("/artefact")
    public ModuleMetaModel provideMetadata(@RequestParam(name = "fullQName") String fullQName) {
        DynamicClassName artifactName = DynamicClassName.forClassName(fullQName);
        if (classRepository.isRegisteredDynamicClass(artifactName)) {
            if (!classRepository.getInfo(artifactName).getSubsystem().isPublic()) {
                throw new FhException(String.format("Artefact '%s' does not exists", fullQName));
            }
        }
        return metaModelService.provideMetadata(fullQName);
    }

    /**
     * Provides metamodel of all artefact in module.
     *
     * @param moduleName module name
     * @return metamodel of artefacts
     */
    @GetMapping("/module")
    public ModuleMetaModel provideModuleMetadata(@RequestParam(name = "moduleName") String moduleName) {
        Subsystem subsystem = ModuleRegistry.getByName(moduleName);
        if (subsystem == null || !subsystem.isPublic()) {
            throw new FhException(String.format("Module '%s' does not exists", moduleName));
        }
        return metaModelService.provideModuleMetadata(moduleName);
    }

    /**
     * Provides metamodel of all artefact in all modules.
     *
     * @return metamodel of artefacts
     */
    @GetMapping("/application")
    public List<ModuleMetaModel> provideApplicationMetadata() {
        return ModuleRegistry.getLoadedModules().stream().filter(Subsystem::isPublic).map(module -> provideModuleMetadata(module.getName())).collect(Collectors.toList());
    }
}
