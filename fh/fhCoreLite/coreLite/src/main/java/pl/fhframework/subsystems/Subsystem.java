package pl.fhframework.subsystems;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.configuration.FHConfiguration;
import pl.fhframework.core.FhSubsystemException;
import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.core.events.ISubsystemLifecycleListener;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.security.ISystemFunctionsMapper;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.subsystems.config.SubsystemConfig;
import pl.fhframework.usecases.dynamic.UseCaseProcess;
import pl.fhframework.usecases.dynamic.UseCaseProcessReader;

import java.io.File;
import java.time.Instant;
import java.util.*;

public abstract class Subsystem {

    static final SubsystemConfig EMPTY_CONFIG = new SubsystemConfig();

    public static final String RESOURCES_DIRECTORY = "dynamicResources";

    static final String ACCESS_PRIVATE = "private";
    static final String ACCESS_PUBLIC = "public";

    @Getter
    private String label;
    /**
     * Subsystem name derived from module.sys file (value of the name attribute from subsystem tag element i.e <subsystem name="fh-designer" ...>)
     */
    @Getter
    private String name;

    private String productLabel;

    @Getter
    private String productUUID;

    @JsonIgnore
    private String access = ACCESS_PRIVATE;

    /**
     * Base path to module's xml files (i.e file [C:\home\projects\pojo\docs\target\classes\).
     * Potentially this will be an overriden path passed as system property.
     */
    @Getter
    @JsonIgnore
    private FhResource basePath;

    /**
     * Base path to module's resources path. By default BASE_PATH/resources.
     */
    @Getter
    @JsonIgnore
    private FhResource resourcesPath;

    /**
     * Base file path to module resources (i.e file [C:\home\projects\pojo\docs\target\classes\).
     * This is alway a real (not overriden) URL to class patch resources of this module.
     */
    @Getter
    @JsonIgnore
    private FhResource baseClassPath;

    @Setter
    @Getter
    @JsonIgnore
    private FhResource configUrl;

    @JsonIgnore
    private List<String> _initiateUseCasesHoldersIds = new ArrayList<>();

    @Getter
    @JsonIgnore
    private List<String> initiateUseCasesHoldersIds = Collections.unmodifiableList(_initiateUseCasesHoldersIds);

    @JsonIgnore
    private Map<String, Object> idPU2SourcePU = new HashMap<>();

    @Getter
    @Setter
    @JsonIgnore
    private ISystemFunctionsMapper systemFunctionsMapper;

    /**
     * Fully qualified names of use cases classes defined in a given module
     */
    @JsonIgnore
    private Set<String> moduleUseCasesNames = new HashSet<>();
    /**
     * Module names this module depends on.
     */
    @JsonIgnore
    private Set<String> dependentModulesNames = new HashSet<>();

    @Setter(AccessLevel.PACKAGE)
    @JsonIgnore
    private SubsystemConfig config;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    @JsonIgnore
    private Instant configTimestamp;

    /**
     * Lifecycle listeners
     */
    @Getter
    @JsonIgnore
    private List<Class<? extends ISubsystemLifecycleListener>> lifecycleListeners = new ArrayList<>();

    protected Subsystem(String name) {
        this(name, name);
    }

    protected Subsystem(String name, String label) {
        this.name = name;
        this.label = label;
        Optional<FhResource> overridenPath = FHConfiguration.getOverridenSubsystemPath(name, true);
        this.basePath = overridenPath.orElse(ReflectionUtils.basePath(this.getClass()));
        this.resourcesPath = basePath.resolve(RESOURCES_DIRECTORY);
        this.baseClassPath = ReflectionUtils.baseClassPath(this.getClass());
        SubsystemConfigUpdater.updateConfigIfNeeded(this);
    }

    protected Subsystem(String name, String label, FhResource basePath, FhResource overridenResourcesPath, FhResource baseClassPath, String dependentModulesNames) {
        this(name, label, null, null, ACCESS_PRIVATE, basePath, overridenResourcesPath, baseClassPath, dependentModulesNames);
    }

    protected Subsystem(String name, String label, String productLabel, String productUUID, String access, FhResource basePath, FhResource overridenResourcesPath, FhResource baseClassPath, String dependentModulesNames) {
        this.name = name;
        this.label = label;
        this.productLabel = productLabel;
        this.productUUID = productUUID;
        this.access = access;
        this.basePath = basePath;
        if (overridenResourcesPath != null) {
            this.resourcesPath = overridenResourcesPath;
        } else {
            this.resourcesPath = basePath.resolve(RESOURCES_DIRECTORY);
        }
        this.baseClassPath = baseClassPath;
        this.dependentModulesNames = parseDependentModules(dependentModulesNames);
        SubsystemConfigUpdater.updateConfigIfNeeded(this);
    }

    private Set<String> parseDependentModules(String dependentModulesNames) {
        if (!StringUtils.isEmpty(dependentModulesNames)) {
            String[] dependentModules = dependentModulesNames.split(",");
            return new HashSet<>(Arrays.asList(dependentModules));
        }
        return Collections.emptySet();
    }

    /**
     * Returns descriptions of subsystem.
     * @return class if subsystem is static, or filePath to *.sys if subsystem is dynamic
     */
    public Object getSource(){
        return this.getClass();
    }

    public long getSourceVersion(){
        return (new Date()).getTime();
    }

    public boolean requiresUpdate(){
        return false;
    }


    public String getBasePackage() {
        return this.getClass().getPackage().getName();
    }

    @JsonIgnore
    public boolean isStatic() {
        return true;
    }

    public String getProductLabel() {
        return StringUtils.isEmpty(productLabel) ? label : productLabel;
    }

    public UseCaseProcess getUseCaseProcess(String useCaseId) {
        try {
            Class<?> clazz = Class.forName(useCaseId);
            if (IUseCase.class.isAssignableFrom(clazz)) {
                return getUseCaseProcess(clazz);
            }else{
                throw new FhUseCaseException("Class '"+clazz.getName()+"' with id '"+useCaseId+"' is not a Use Case!");
            }
        } catch (ClassNotFoundException e) {
            String ducFilePath = getSubsystemResourcePath(useCaseId, ".duc");
            return getUseCaseProcess(ducFilePath, useCaseId);
        }
    }

    private UseCaseProcess getUseCaseProcess(String ducFilePath, String useCaseId) {
        if (ducFilePath == null){
            throw new FhSubsystemException("Error while configure subsystem described by '" + getSource() + "' - couldn't find static class '" + useCaseId + "' or path to dynamic file!");
        }
        File file = new File(ducFilePath);
        if (file.exists() && file.isFile()) {
            return UseCaseProcessReader.instance.read(file);
        } else {
            throw new FhSubsystemException("Error while configure subsystem described by '" + getSource() + "' -  couldn't find file '" + ducFilePath + "' or static class '" + useCaseId + "'");
        }
    }

    public UseCaseProcess getUseCaseProcess(Class useCaseId){
        //TODO: implement.
        throw new FhSubsystemException("Functionality of indicating static use cases in dynamic subsystem is not implemented!");
    }


    private final Map<String, String> idSource2Path = new HashMap<>();

    private String getSubsystemResourcePath(String resourceId, String resourceType) {
        String returnedValue = idSource2Path.get(resourceId);
        if (returnedValue==null && !idSource2Path.containsKey(resourceId)) {
            returnedValue = basePath.toExternalPath();
            idSource2Path.put(resourceId, returnedValue);
        }
        return returnedValue;
    }


    @Override
    public String toString() {
        return getSource()+"{" +
                "label='" + label + '\'' +
                '}';
    }

    public void addStaticUseCaseReference(Class<? extends IUseCase> reference) {
        String id = reference.getName();
        _initiateUseCasesHoldersIds.add(id);
        idPU2SourcePU.put(id, reference);
    }

    public void addUseCaseReference(String reference){
        _initiateUseCasesHoldersIds.add(reference);
        try{
            Class<? extends IUseCase> staticClazz = (Class<? extends IUseCase>) Class.forName(reference);
            idPU2SourcePU.put(reference, staticClazz);
        } catch (ClassNotFoundException e) {
            //Calculating path of described dynamic PU with given id.
            String PPUFilePath = getSubsystemResourcePath(reference, ".duc");
            idPU2SourcePU.put(reference, PPUFilePath);
        }
    }

    public Object getUseCaseSource(String useCaseId){
        return idPU2SourcePU.get(useCaseId);
    }

    private Map<String, DynamicSubsystem.CachedClass> _classCache = new HashMap<>();

    @JsonIgnore
    public List<Class<IInitialUseCase>> getStaticUseCaseInitializersList() {
        List<Class<IInitialUseCase>> result = new ArrayList<>();
        for (String idPU : _initiateUseCasesHoldersIds) {
            Object source = getUseCaseSource(idPU);
            if (source instanceof Class){
                Class<?> clazz = (Class<?>) source;
                if (IInitialUseCase.class.isAssignableFrom(clazz)) {
                    result.add((Class<IInitialUseCase>) clazz);
                }
            }
        }
        return result;
    }

    /**
     * Adds a lifecycle listener
     * @param listenerClass listener class
     */
    public void addlifecycleListener(Class<? extends ISubsystemLifecycleListener> listenerClass) {
        this.lifecycleListeners.add(listenerClass);
    }

    /**
     * Add given use case class to given subsystem use case registry
     * @param useCaseClazz use case class
     */
    public void addUseCase(Class<? extends IUseCase> useCaseClazz) {
        moduleUseCasesNames.add(useCaseClazz.getName());
}

    /**
     * Return unmodifiable view of module use cases
     * @return module use cases
     */
    @JsonIgnore
    public Set<String> getModuleUseCasesNames() {
        return Collections.unmodifiableSet(moduleUseCasesNames);
    }

    /**
     * Returns <tt>true</tt> if this module contains use case with the specified name.
     *
     * @param fullyQualifiedUseCaseClassName use case name
     * @return <tt>true</tt> if this module contains the specified use case
     */
    public boolean containsUseCase(String fullyQualifiedUseCaseClassName) {
        return moduleUseCasesNames.contains(fullyQualifiedUseCaseClassName);
    }

    /**
     * Return unmodifiable view of dependent module names this module depends on
     * @return dependent module names
     */
    public Set<String> getDependentModulesNames() {
        return Collections.unmodifiableSet(dependentModulesNames);
    }

    /**
     * Returns subsystem's configuration that is kept in module.xml file and may change at runtime.
     * @return subsystem's configuration
     */
    @JsonIgnore
    public SubsystemConfig getConfig() {
        if (config == null) {
            return EMPTY_CONFIG;
        } else {
            return config;
        }
    }

    @JsonIgnore
    public boolean isPublic() {
        return ACCESS_PUBLIC.equals(access);
    }
}
