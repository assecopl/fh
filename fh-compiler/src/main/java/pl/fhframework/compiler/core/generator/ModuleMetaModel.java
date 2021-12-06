package pl.fhframework.compiler.core.generator;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.generator.model.MetaModel;
import pl.fhframework.compiler.core.generator.model.data.ClassMm;
import pl.fhframework.compiler.core.generator.model.form.FormMm;
import pl.fhframework.compiler.core.generator.model.rule.RuleMm;
import pl.fhframework.compiler.core.generator.model.service.EndpointMm;
import pl.fhframework.compiler.core.generator.model.service.ServiceMm;
import pl.fhframework.compiler.core.generator.model.usecase.UseCaseMm;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import java.util.*;

public class ModuleMetaModel {
    @Getter
    private String appName = "ngApp"; // todo:
    @Getter
    private final Subsystem module;

    @Getter
    @Setter
    private String initialPrimaryUc;

    @JsonIgnore
    Map<String, ClassMm> classes = new TreeMap<>();
    @JsonIgnore
    Map<String, RuleMm> rules = new TreeMap<>();
    @JsonIgnore
    Map<String, ServiceMm> services = new TreeMap<>();
    @JsonIgnore
    Map<String, FormMm> forms = new TreeMap<>();
    @JsonIgnore
    Map<String, UseCaseMm> usecases = new TreeMap<>();

    @Getter
    List<EndpointMm> endpoints = new ArrayList<>();

    // Stores external dependencies (from other modules or same module, but ungenerable - provided),
    // key is fully qualified name (eg. pl.gov.repo.Person), value is type Dependency
    @JsonIgnore
    Map<String, Dependency> externalDependenciesMap = new TreeMap<>();

    public ModuleMetaModel(Subsystem module) {
        this.module = module;
    }

    @JsonGetter
    public Collection<ClassMm> getModelClasses() {
        return classes.values();
    }

    @JsonGetter
    public Collection<RuleMm> getRules() {
        return rules.values();
    }

    @JsonGetter
    public Collection<ServiceMm> getServices() {
        return services.values();
    }

    @JsonGetter
    public Collection<FormMm> getForms() {
        return forms.values();
    }

    @JsonGetter
    public Collection<UseCaseMm> getUseCases() {
        return usecases.values();
    }

    public <T> Collection<T> getMetadata(DynamicClassArea type) {
        switch (type) {
            case MODEL:
                return (Collection<T>) classes.values();
            case RULE:
                return (Collection<T>) rules.values();
            case SERVICE:
                return (Collection<T>) services.values();
            case FORM:
                return (Collection<T>) forms.values();
            case USE_CASE:
                return (Collection<T>) usecases.values();
            default:
        }

        throw new IllegalArgumentException("Unknown metadata type");
    }

    public <T extends MetaModel> T getMetadata(String name) {
        // todo: metadata to external artefacts
        if (classes.containsKey(name)) {
            return (T) classes.get(name);
        }
        if (rules.containsKey(name)) {
            return (T) rules.get(name);
        }
        if (services.containsKey(name)) {
            return (T) services.get(name);
        }
        if (forms.containsKey(name)) {
            return (T) forms.get(name);
        }
        if (usecases.containsKey(name)) {
            return (T) usecases.get(name);
        }
        if (isExternalDependency(name)) {
            Optional<Object> metadata = getExternalDependency(name).get().getMetadata();
            if (metadata.isPresent()) {
                return (T) metadata.get();
            }
        }
        return null;
    }

    public static Optional<DynamicClassArea> getMetadataType(Object metadata) {
        if (metadata instanceof ClassMm) {
            return Optional.of(DynamicClassArea.MODEL);
        }
        if (metadata instanceof RuleMm) {
            return Optional.of(DynamicClassArea.RULE);
        }
        if (metadata instanceof ServiceMm) {
            return Optional.of(DynamicClassArea.SERVICE);
        }
        if (metadata instanceof FormMm) {
            return Optional.of(DynamicClassArea.FORM);
        }
        if (metadata instanceof UseCaseMm) {
            return Optional.of(DynamicClassArea.USE_CASE);
        }

        return Optional.empty();
    }

    @JsonGetter
    public Collection<Dependency> getExternalDependencies() {
        return this.externalDependenciesMap.values();
    }

    public Dependency getDependency(String fullName) {
        fullName = DynamicClassName.forClassName(fullName).getOuterClassName().toFullClassName();

        if (isExternalDependency(fullName)) {
            return getExternalDependency(fullName).get();
        }
        Object metadata = getMetadata(fullName);
        if (metadata == null && fullName.startsWith("pl.fhframework")) { // todo: internal dependency, e.g. ViewEvent
            return Dependency.builder().name(fullName).provided(true).core(true).build();
        }
        if (metadata == null && isStatic(fullName)) // todo: remove when static classes are proxied, then they will appeare as externaldependencies
        {
            return Dependency.builder().name(fullName).provided(true).module(ModuleRegistry.getByName(ModuleRegistry.getModuleId(DynamicClassName.forClassName(fullName)))).build();
        }
        DynamicClassArea dca = getMetadataType(metadata).get();
        return Dependency.builder().name(fullName).metadata(metadata).module(module).type(dca).build();
    }

    // todo: remove when static classes are proxied, then they will appeare as externaldependencies
    private boolean isStatic(String fullName) {
        return ReflectionUtils.tryGetClassForName(fullName) != null;
    }

    public Optional<Dependency> getExternalDependency(String fullName) {
        return Optional.ofNullable(this.externalDependenciesMap.get(fullName));
    }

    public boolean isExternalDependency(String fullName) {
        return this.externalDependenciesMap.containsKey(fullName);
    }

    // WARNING: ORDER OF THESE ELEMENTS IS IMPORTANT!
    // CLASSES ARE COMPILED IN THIS ORDER AND CANNOT DEPEND ON CLASSES FROM AREAS PLACES LATER ON THIS LIST.
    @JsonIgnore
    public List<DynamicClassArea> getDefaultDependencyOrder() {
        return Arrays.asList(DynamicClassArea.MODEL, DynamicClassArea.RULE, DynamicClassArea.SERVICE, DynamicClassArea.FORM, DynamicClassArea.USE_CASE);
    }

    void addMetadata(String fullName, Object metadata) {
        if (metadata instanceof ClassMm) {
            classes.put(fullName, (ClassMm) metadata);
        }
        else if (metadata instanceof RuleMm) {
            rules.put(fullName, (RuleMm) metadata);
        }
        else if (metadata instanceof ServiceMm) {
            services.put(fullName, (ServiceMm) metadata);
        }
        else if (metadata instanceof FormMm) {
            forms.put(fullName, (FormMm) metadata);
        }
        else if (metadata instanceof UseCaseMm) {
            usecases.put(fullName, (UseCaseMm) metadata);
        }
        else {
            throw new IllegalArgumentException("Unknown metadata type");
        }
    }

    void addExternalDependency(Dependency dependency) {
        externalDependenciesMap.put(dependency.getName(), dependency);
    }

}
