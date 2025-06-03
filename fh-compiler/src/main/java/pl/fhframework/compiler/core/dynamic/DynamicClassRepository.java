package pl.fhframework.compiler.core.dynamic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyTimestampJavaGenerator;
import pl.fhframework.compiler.core.dynamic.event.DynamicClassChangedEvent;
import pl.fhframework.compiler.core.dynamic.event.DynamicClassRemovedEvent;
import pl.fhframework.compiler.core.generator.DynamicClassCompiler;
import pl.fhframework.compiler.core.generator.RulesTypeProvider;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.security.AuthorizationManagerExt;
import pl.fhframework.compiler.core.services.DynamicServiceMetadata;
import pl.fhframework.compiler.core.uc.dynamic.model.DynamicUseCaseMetadata;
import pl.fhframework.compiler.forms.FhFormGeneratorException;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.aspects.snapshots.SnapshotsModelAspect;
import pl.fhframework.core.*;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.model.forms.AdHocForm;
import pl.fhframework.model.forms.Form;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Central dynamic class repository
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) // must be a singleton
public class DynamicClassRepository implements ApplicationListener<ContextRefreshedEvent>, IDynamicClassResolver {

    public static final String PRECOMPILED_CLASS_SUFFIX = "_Precompiled";

    public static final DynamicClassArea[][] AREA_COMPILATION_ORDER = {{DynamicClassArea.MODEL}, {DynamicClassArea.RULE, DynamicClassArea.SERVICE}, {DynamicClassArea.FORM}, {DynamicClassArea.JR_REPORT}, {DynamicClassArea.USE_CASE}};

    private static final Map<DynamicClassName, StaticClassRepositoryEntry> STATIC_CLASSES = new ConcurrentHashMap<>();

    private static final Map<DynamicClassName, DynamicClassRepositoryEntry> DYNAMIC_CLASSES = new ConcurrentHashMap<>();

    private static final Map<DynamicClassName, Set<DynamicClassName>> DYNAMIC_CLASS_DEPENDANT_CLASSES = new ConcurrentHashMap<>();

    private static final long MAX_FILE_CHECK_FREQUNECY = 5_000; // 5s

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).withLocale(Locale.getDefault());

    @Getter
    @Setter
    @AllArgsConstructor
    static class StaticClassRepositoryEntry implements IClassInfo {

        /**
         * Class name
         */
        private DynamicClassName className;

        /**
         * Dynamic class area
         */
        private DynamicClassArea area;

        /**
         * Subsystem
         */
        private Subsystem subsystem;

        private Class<?> staticClass;

        @Override
        public DynamicClassFileDescriptor getXmlFile() {
            return null;
        }

        @Override
        public boolean isDynamic() {
            return false;
        }
    }

    public static class DependenciesNotResolvableException extends Exception {
        public DependenciesNotResolvableException(String message) {
            super(message);
        }
    }

    /**
     * Form class cache repository entry
     */
    @Getter
    @Setter
    static class DynamicClassRepositoryEntry<M extends DynamicClassMetadata> implements IClassInfo {

        /**
         * Metadata containing common and area specific data.
         */
        private M metadata;

        /**
         * XML file's URL (full path, may be in JAR)
         */
        DynamicClassFileDescriptor xmlFile;

        /**
         * Cached compiled class (precompiled or runtime compiled)
         */
        private Class<?> cachedClass;

        /**
         * XML (this class file and all dependecies files) modification timestamps of this cached class.
         */
        private Map<DynamicClassName, Instant> cachedClassAndDepencenciesXMLTimestamps;

        /**
         * Last knonw XML modification timestamp.
         */
        private Instant lastKnownXMLTimestamp;

        /**
         * Last XML modification check.
         */
        private Instant lastXMLChangeCheckTimestamp;

        /**
         * Last XML modification timestamp when metadata was read.
         */
        private Instant metadataXMLTimestamp;

        /**
         * Next version to be used
         */
        private int nextVersion = 1;

        /**
         * Dynamic class area
         */
        private DynamicClassArea area;

        private boolean valid = true;

        @Override
        public Subsystem getSubsystem() {
            return xmlFile.getSubsystem();
        }

        @Override
        public DynamicClassName getClassName() {
            return getMetadata().getDynamicClassName();
        }

        @Override
        public boolean isDynamic() {
            return true;
        }

        @Override
        public Instant getLastModification() {
            return metadataXMLTimestamp;
        }
    }

    @Autowired
    private DynamicClassCompiler dynamicClassCompiler;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private List<AbstractDynamicClassAreaHandler> domainHandlerList;
    private Map<DynamicClassArea, AbstractDynamicClassAreaHandler> domainHandlers = new HashMap<>();

    @Autowired(required = false)
    private RulesTypeProvider rulesTypeProvider;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired(required = false)
    private AuthorizationManagerExt authorizationManager;

    private Set<DynamicClassFileDescriptor> alreadyRegisteredFiles = new HashSet<>();

    private boolean started = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent refreshedEvent) {
        if (!started) {
            start();
        }
    }

    protected synchronized void start() {
        // put handlers to area -> handler map
        for (AbstractDynamicClassAreaHandler handler : domainHandlerList) {
            domainHandlers.put(handler.getSupportedArea(), handler);
        }

        // area static classes
        for (AbstractDynamicClassAreaHandler<?> handler : domainHandlerList) {
            for (Subsystem loadedSubsystem : ModuleRegistry.getLoadedModules()) {
                for (Class<?> staticClass : handler.listAreaStaticClasses(loadedSubsystem)) {
                    DynamicClassName className = DynamicClassName.forStaticBaseClass(staticClass);
                    STATIC_CLASSES.put(className, new StaticClassRepositoryEntry(className, handler.getSupportedArea(), loadedSubsystem, staticClass));
                }
            }
        }

        // area dynamic classes
        for (AbstractDynamicClassAreaHandler handler : domainHandlerList) {
            for (Subsystem loadedSubsystem : ModuleRegistry.getLoadedModules()) {
                readSubsystemDynamicClasses(loadedSubsystem, handler, true);
            }
        }

        started = true;

        for (AbstractDynamicClassAreaHandler handler : domainHandlerList) {
            handler.postAllLoad(this);
        }
    }

    public void autoscanForNewDynamicClasses(Subsystem subsystem, DynamicClassArea area) {
        if (started) {
            // check for deleted files
            DYNAMIC_CLASSES.forEach((dynamicClassName, repositoryEntry) -> {
                if (repositoryEntry.getArea() == area
                        && repositoryEntry.getSubsystem().getName().equals(subsystem.getName())
                        && !repositoryEntry.getXmlFile().getResource().exists()) {
                    FhLogger.info(this.getClass(), "Unregistering dynamic class: {} as its XML file has been deleted: {}.",
                            dynamicClassName.toFullClassName(), repositoryEntry.getXmlFile().getResource().toString());
                    unregisterDynamicClassFile(dynamicClassName, false);
                }
            });

            // check for new files
            readSubsystemDynamicClasses(subsystem, getAreaHandler(area), false);
        }
    }

    protected <M extends DynamicClassMetadata> void readSubsystemDynamicClasses(Subsystem subsystem,
                                                                                AbstractDynamicClassAreaHandler<M> areaHandler,
                                                                                boolean logAll) {
        DynamicClassArea area = areaHandler.getSupportedArea();
        String basePackage = areaHandler.isSearchInBasePackageOnly() ? subsystem.getBasePackage() : null;
        FhResource basePath = subsystem.getBasePath();
        if (logAll) {
            FhLogger.debug(this.getClass(), loggerView -> loggerView.log("Reading {} subsystem dynamic classes from {} area.", subsystem.getName(), area));
        }

        List<DynamicClassFileDescriptor> allFiles = listNewDynamicClassFiles(
                basePath,
                basePackage,
                areaHandler.getXmlFilenameExtension(),
                subsystem);

        if (logAll || !allFiles.isEmpty()) {
            FhLogger.info(this.getClass(), "Found {} dynamic class files for {} subsystem from {} area.",
                    allFiles.size(), subsystem.getName(), area);
        }
        DynamicClassName dynamicClassName;
        for (DynamicClassFileDescriptor file : allFiles) {
            try {
                dynamicClassName = registerDynamicClassFile(file, area);
                if (area == DynamicClassArea.USE_CASE || area == DynamicClassArea.RULE || area == DynamicClassArea.SERVICE) {
                    refreshAuthorizationInfo(subsystem, dynamicClassName);
                }
            } catch (Throwable e) {
                FhLogger.error("Error registering {} XML as a {}", file.getResource().getDescription(), areaHandler.getSupportedArea(), e);
            }
        }
        areaHandler.postLoad();
    }

    /**
     * Reads from dynamic use case class information about permissions for use case and actions
     * and adds it into authorization cache.
     *
     * @param subsystem        subsystem, where the dynamic use case was registered
     * @param dynamicClassName name of the dynamic use case class.
     */
    public void refreshAuthorizationInfo(Subsystem subsystem, DynamicClassName dynamicClassName) {
        if (authorizationManager != null) {
            DynamicClassRepositoryEntry entry = DYNAMIC_CLASSES.get(dynamicClassName);
            if (entry != null) {
                if (entry.getMetadata() instanceof DynamicUseCaseMetadata) {
                    authorizationManager.registerDynamicUseCase(
                            subsystem,
                            ((DynamicUseCaseMetadata) entry.getMetadata()).getDynamicUseCase()
                    );
                } else if (entry.getMetadata() instanceof DynamicRuleMetadata) {
                    authorizationManager.registerDynamicRule(
                            subsystem,
                            ((DynamicRuleMetadata) entry.getMetadata()).getRule()
                    );
                } else if (entry.getMetadata() instanceof DynamicServiceMetadata) {
                    authorizationManager.registerDynamicService(
                            subsystem,
                            ((DynamicServiceMetadata) entry.getMetadata()).getService()
                    );
                }
            }
        }
    }

    <M extends DynamicClassMetadata> AbstractDynamicClassAreaHandler<M> getAreaHandler(DynamicClassArea area) {
        return domainHandlers.get(area);
    }

    /**
     * Registers dynamic class in the repository
     *
     * @param file             file descriptor
     * @param dynamicClassArea area of this dynamic class
     */
    public synchronized DynamicClassName registerDynamicClassFile(DynamicClassFileDescriptor file, DynamicClassArea dynamicClassArea) {
        DynamicClassRepositoryEntry repositoryEntry = toDynamicClassRepositoryEntry(file, dynamicClassArea);
        DynamicClassMetadata metadata = refreshMetadataIfNeeded(repositoryEntry);

        // build repository entry
        DynamicClassName className = metadata.getDynamicClassName();
        if (DYNAMIC_CLASSES.containsKey(className)) {
            DynamicClassRepositoryEntry<DynamicClassMetadata> duplicate = toDynamicClassRepositoryEntry(className);
            throw new FhFrameworkException("Class " + className.toFullClassName() + " already defined as " + duplicate.getArea());
        }

        fillRepositoryEntryFromExistingClasses(repositoryEntry, className);
        DYNAMIC_CLASSES.put(className, repositoryEntry);

        getAreaHandler(dynamicClassArea).postRegisterDynamicClass(repositoryEntry.getMetadata());

        alreadyRegisteredFiles.add(file);

        return className;
    }

    /**
     * Finds dependant dynamic classes of a given class
     *
     * @param dynamicClassName  dynamic class
     * @param externalAreasOnly if search only in areas other than this class (for static classes it lists all areas)
     * @return set of dependant dynamic classes of a given class
     */
    public synchronized Collection<IClassInfo> findDependantClasses(DynamicClassName dynamicClassName, boolean externalAreasOnly) {
        DynamicClassArea myArea = getClassArea(dynamicClassName);
        List<IClassInfo> dependantClasses = new ArrayList<>();
        for (DynamicClassName dependantClass : getDependantClassesSet(dynamicClassName)) {
            DynamicClassRepositoryEntry<?> dependantEntry = DYNAMIC_CLASSES.get(dependantClass);
            if (dependantEntry != null && (myArea != dependantEntry.area || !externalAreasOnly)) {
                dependantClasses.add(dependantEntry);
            }
        }
        return dependantClasses;
    }

    /**
     * Informs repository that dynamic class' XML file changed.
     *
     * @param dynamicClassName dynamic class name
     */
    public synchronized void updateDynamicClass(DynamicClassName dynamicClassName) {
        DynamicClassRepositoryEntry repositoryEntry = toDynamicClassRepositoryEntry(dynamicClassName);

        getAreaHandler(repositoryEntry.getArea()).preUpdateDynamicClass(repositoryEntry.getMetadata());

        refreshMetadataIfNeeded(repositoryEntry);

        getAreaHandler(repositoryEntry.getArea()).postUpdateDynamicClass(repositoryEntry.getMetadata());

        eventPublisher.publishEvent(new DynamicClassChangedEvent(dynamicClassName, repositoryEntry.getArea()));
    }

    /**
     * Converts new source artifact to dynamic repository entry
     *
     * @param file             Source file
     * @param dynamicClassArea Application module
     * @return Dynamic repository entry
     */
    DynamicClassRepositoryEntry toDynamicClassRepositoryEntry(DynamicClassFileDescriptor file, DynamicClassArea dynamicClassArea) {
        DynamicClassRepositoryEntry<DynamicClassMetadata> result = new DynamicClassRepositoryEntry<>();
        result.setXmlFile(file);
        result.setArea(dynamicClassArea);

        return result;
    }

    /**
     * Converts already registered class to dynamic repository entry
     *
     * @param dynamicClassName Alreadyt existing class name
     * @return Dynamic repository entry
     */
    DynamicClassRepositoryEntry toDynamicClassRepositoryEntry(DynamicClassName dynamicClassName) {
        DynamicClassRepositoryEntry result = DYNAMIC_CLASSES.get(dynamicClassName);
        if (result == null) {
            throw new FhException("Not a registered dynamic class: " + dynamicClassName);
        }

        return result;
    }

    /**
     * Checks whether all dependencies of artifact are resolvable
     *
     * @param metadata Artifact repository entry
     * @param importingDcn
     * @throws DependenciesNotResolvableException Thrown when one of the dependencies is not resolvable
     */
    void areAllDependenciesResolvable(DynamicClassMetadata metadata, Set<DynamicClassName> importingDcn) throws DependenciesNotResolvableException {
        if (metadata == null) {
            return;
        }
        for (DynamicClassName dependency : metadata.getDependencies()) {
            boolean isDynamicClass = isRegisteredDynamicClass(dependency);

            if (isDynamicClass) {
                continue;
            }

            boolean isStaticClass = isRegisteredStaticClass(dependency);

            if (isStaticClass) {
                continue;
            }

            if (importingDcn.contains(dependency)) {
                continue;
            }

            try {
                FhCL.classLoader.loadClass(dependency.getOuterClassName().toFullClassName());
            } catch (ClassNotFoundException e) {
                throw new DependenciesNotResolvableException(
                        String.format("Dependant class '%s' not found for '%s'. Cannot import file.",
                                dependency.getOuterClassName().toFullClassName(),
                                metadata.getDynamicClassName().toFullClassName()
                        ));
            }
        }
    }

    /**
     * Tries to unregister dynamic class from repository. If may fail if dependant classes exist.
     *
     * @param dynamicClassName  dynamic class
     * @param checkDependencies if should check form dependant classes
     * @return true, if no dependant classes where found and class has bean unregistered
     */
    public synchronized boolean unregisterDynamicClassFile(DynamicClassName dynamicClassName, boolean checkDependencies) {
        if (checkDependencies && !getDependantClassesSet(dynamicClassName).isEmpty()) {
            FhLogger.error("Class has dependant classes: " +
                    getDependantClassesSet(dynamicClassName).stream().map(DynamicClassName::toFullClassName).collect(Collectors.joining(", ")));
            return false;
        }
        // remove this class as dependant from dependency classes
        for (DynamicClassName dependency : DYNAMIC_CLASSES.get(dynamicClassName).getMetadata().getDependencies()) {
            if (DYNAMIC_CLASS_DEPENDANT_CLASSES.containsKey(dependency)) {
                DYNAMIC_CLASS_DEPENDANT_CLASSES.get(dependency).remove(dynamicClassName);
            }
        }
        DynamicClassRepositoryEntry repositoryEntry = DYNAMIC_CLASSES.remove(dynamicClassName);
        DYNAMIC_CLASS_DEPENDANT_CLASSES.remove(dynamicClassName);
        alreadyRegisteredFiles.remove(repositoryEntry.getXmlFile());
        getAreaHandler(repositoryEntry.getArea()).postUnregisterDynamicClass(repositoryEntry.getMetadata());
        eventPublisher.publishEvent(new DynamicClassRemovedEvent(dynamicClassName, repositoryEntry.getArea()));

        return true;
    }

    // TODO: change Path to URL
    private List<DynamicClassFileDescriptor> listNewDynamicClassFiles(FhResource sourceDirectory, String basePackage, String xmlFileNameExtension, Subsystem subsystem) {
        FhResource basePackagePath = sourceDirectory;
        if (basePackage != null) {

            basePackagePath = basePackagePath.createRelative(basePackage.replace('.', File.separatorChar));
        }

        List<DynamicClassFileDescriptor> foundFiles = new ArrayList<>();
        basePackagePath.walkFileAndTree(new FileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().endsWith(xmlFileNameExtension)) {
                    DynamicClassFileDescriptor foundFile = DynamicClassFileDescriptor.forPath(
                            file, Paths.get(sourceDirectory.toExternalPath()).relativize(file).toString(), subsystem);
                    if (!alreadyRegisteredFiles.contains(foundFile)) {
                        foundFiles.add(foundFile);
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                FhLogger.warn("Cannot check {} - {}: {}", file.toAbsolutePath().toString(), exc.getClass().getSimpleName(), exc.getMessage());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        return foundFiles;
    }

    /**
     * Checks if a class is registered which means it is a dynamic class.
     *k
     * @param dynamicClassName class name
     * @return true if is registered
     */
    public boolean isRegisteredDynamicClass(DynamicClassName dynamicClassName) {
        if (!started) {
            throw new IllegalStateException("Not started yet! Please use start() to start the repository.");
        }
        // look only for outer class name
        dynamicClassName = dynamicClassName.getOuterClassName();
        return containsDynamicClassIgnoreCase(DYNAMIC_CLASSES.keySet(), dynamicClassName);
    }

    /**
     * Checks if a class is registered as static class.
     *
     * @param dynamicClassName class name
     * @return true if is registered
     */
    public boolean isRegisteredStaticClass(DynamicClassName dynamicClassName) {
        if (!started) {
            throw new IllegalStateException("Not started yet! Please use start() to start the repository.");
        }
        // look only for outer class name
        dynamicClassName = dynamicClassName.getOuterClassName();
        return STATIC_CLASSES.containsKey(dynamicClassName);
    }

    /**
     * t
     * Lists registered dynamic classes from the given area.
     *
     * @param area area
     * @return list of registered dynamic classes from the given area
     */
    public List<IClassInfo> listClasses(DynamicClassFilter filter, DynamicClassArea area) {
        List<IClassInfo> list = new ArrayList<>();
        for (DynamicClassRepositoryEntry repositoryEntry : DYNAMIC_CLASSES.values()) {
            if (repositoryEntry.getArea() == area && filter.test(repositoryEntry)) {
                list.add(repositoryEntry);
            }
        }
        for (StaticClassRepositoryEntry repositoryEntry : STATIC_CLASSES.values()) {
            if (repositoryEntry.getArea() == area && filter.test(repositoryEntry)
                    && !DYNAMIC_CLASSES.containsKey(repositoryEntry.className)) { // to avoid duplicates with extending dynamic classes
                list.add(repositoryEntry);
            }
        }
        Collections.sort(list, DynamicClassFilter.COMPARATOR_SORT_BY_CLASS_NAME);
        return list;
    }

    public Class<?> getOrCompileDynamicClass(DynamicClassName dynamicClassName) {
        if (!started) {
            throw new IllegalStateException("Not started yet! Please use start() to start the repository.");
        }

        // split into outer and inner class
        Optional<String> innerClassName = dynamicClassName.getInnerClassName();
        dynamicClassName = dynamicClassName.getOuterClassName();

        DynamicClassRepositoryEntry repositoryEntry = DYNAMIC_CLASSES.get(dynamicClassName);
        if (repositoryEntry == null) {
            throw new FhException("Dynamic class not registered: " + dynamicClassName);
        }

        AbstractDynamicClassAreaHandler handler = getAreaHandler(repositoryEntry.getArea());

        // initial check to avoid synchronization
        if (didTimestampsChange(dynamicClassName, repositoryEntry, false)) {
            synchronized (this) {
                // final check while being synchronized
                if (didTimestampsChange(dynamicClassName, repositoryEntry, false)) {
                    Map<DynamicClassArea, List<DynamicClassName>> compilationAreaMap = collectClassesForRecompilation(dynamicClassName);
                    for (DynamicClassArea[] dynamicClassAreaList : AREA_COMPILATION_ORDER) {
                        Map<AbstractDynamicClassAreaHandler, List<DynamicClassName>> classesForCompilationMap = new HashMap<>();
                        for (DynamicClassArea dynamicClassArea : dynamicClassAreaList) {
                            List<DynamicClassName> classesForCompilation = compilationAreaMap.get(dynamicClassArea);
                            if (classesForCompilation != null && classesForCompilation.size() > 0) {
                                classesForCompilationMap.put(getAreaHandler(dynamicClassArea), classesForCompilation);
                            }
                        }
                        if (!classesForCompilationMap.isEmpty()) {
                            generateAndCompileClasses(classesForCompilationMap);
                            if (Arrays.asList(dynamicClassAreaList).contains(DynamicClassArea.RULE) && rulesTypeProvider != null) {
                                rulesTypeProvider.refresh();
                            }
                        }
                    }
                }
            }
        }

        // if we got here, it means that compilation was not needed or was just done
        Class<?> readyClass = repositoryEntry.cachedClass;

        // optionally get the inner class
        return getOptionalInnerClass(readyClass, innerClassName);
    }

    /**
     * Returns info for a registered class.
     *
     * @param dynamicClassName class name to look for in repo
     * @return return info for a registered class
     */
    public synchronized IClassInfo getInfo(DynamicClassName dynamicClassName) {
        IClassInfo info = DYNAMIC_CLASSES.get(dynamicClassName);
        if (info == null) {
            info = STATIC_CLASSES.get(dynamicClassName);
        }
        if (info == null) {
            throw new NullPointerException("Dynamic class " + dynamicClassName + " is not in the repository. Register it first.");
        }
        return info;
    }

    /**
     * Returns if class is valid.
     *
     * @param dynamicClassName class name to look for in repo
     * @return return info if class is valid
     */
    public synchronized boolean isValid(DynamicClassName dynamicClassName) {
        return !started || getInfo(dynamicClassName).isValid();
    }


    /**
     * Returns info for a registered class.
     *
     * @param dynamicClassName class name to look for in repo
     * @return return info for a registered class
     */
    public synchronized Class<?> getStaticClass(DynamicClassName dynamicClassName) {
        IClassInfo info = STATIC_CLASSES.get(dynamicClassName);
        if (info == null) {
            throw new NullPointerException("Static class " + dynamicClassName + " is not in the repository. Register it first.");
        }
        return StaticClassRepositoryEntry.class.cast(info).getStaticClass();
    }

    /**
     * Returns metadata for a dynamic class.
     *
     * @param dynamicClassName class name to look for in repo
     * @return return metadata for a specific dynamic metadata area, like DynamicClassMetadata.
     */
    public synchronized <M extends DynamicClassMetadata> M getMetadata(DynamicClassName dynamicClassName) {
        DynamicClassRepositoryEntry repositoryEntry = DYNAMIC_CLASSES.get(dynamicClassName);
        if (repositoryEntry == null) {
            throw new NullPointerException("Dynamic class " + dynamicClassName + " is not in the repository. Register it first.");
        }
        refreshMetadataIfNeeded(repositoryEntry);
        return (M) repositoryEntry.getMetadata();
    }

    /**
     * Creates dependency context for provided dependencies. May cause compilation of those dependencies and their dependencies.
     *
     * @param dependencies dependencies
     * @return dependency context
     */
    public synchronized DependenciesContext resolveDependencies(Collection<DynamicClassName> dependencies) {
        DependenciesContext dependenciesContext = new DependenciesContext();
        resolveDependencies(dependenciesContext, dependencies, Collections.emptyList(), true, true);

        return dependenciesContext;
    }

    /**
     * Returns file descriptors for class and dependent dynamic classes
     *
     * @param dynamicClassName class name to look for in repo
     * @return file descriptor for a specific dynamic metadata area, like DynamicClassMetadata and dependencies
     */
    public ArtifactExportModel getWithDependencies(DynamicClassName dynamicClassName) {
        ArtifactExportModel exportModel = new ArtifactExportModel();

        DynamicClassFileDescriptor descriptor = this.getFileDescriptor(dynamicClassName);
        exportModel.setSubsystem(descriptor.getSubsystem());

        getWithDependencies(dynamicClassName, exportModel, new HashSet<>());
        getImagesForArtifacts(exportModel);
        getMarkdownFilesForArtifacts(exportModel);

        return exportModel;
    }

    private void getImagesForArtifacts(ArtifactExportModel exportModel) {
        Pattern pattern = Pattern.compile("image\\?module=.+&amp;path=([\\w ().[^\"]]*)\"");

        for (DynamicClassFileDescriptor desc : exportModel.getDescriptors()) {
            if (!FilenameUtils.isExtension(desc.getResource().getFilename(), "frm")) {
                continue;
            }

            String contents = new String(desc.getResource().getContent());
            Matcher matcher = pattern.matcher(contents);
            while (matcher.find()) {
                ArtifactExportModel.ImageDescriptor image = new ArtifactExportModel.ImageDescriptor(matcher.group(1), desc.getSubsystem());
                exportModel.getImages().add(image);
            }
        }
    }

    private void getMarkdownFilesForArtifacts(ArtifactExportModel exportModel) {
        Pattern pattern = Pattern.compile("markdown\\?module=.+&amp;path=([\\w ().[^\"]]*)\"");

        for (DynamicClassFileDescriptor desc : exportModel.getDescriptors()) {
            if (!FilenameUtils.isExtension(desc.getResource().getFilename(), "frm")) {
                continue;
            }

            String contents = new String(desc.getResource().getContent());
            Matcher matcher = pattern.matcher(contents);
            while (matcher.find()) {
                ArtifactExportModel.MarkdownDescriptor markdownFile = new ArtifactExportModel.MarkdownDescriptor(matcher.group(1), desc.getSubsystem());
                exportModel.getMarkdownFiles().add(markdownFile);
            }
        }
    }

    private void getWithDependencies(DynamicClassName dynamicClassName, ArtifactExportModel exportModel, Set<DynamicClassName> visitedClasses) {

        DynamicClassMetadata metadata;

        try {
            DynamicClassFileDescriptor descriptor = this.getFileDescriptor(dynamicClassName);

            exportModel.getDescriptors().add(descriptor);
            visitedClasses.add(dynamicClassName);
            metadata = this.getMetadata(dynamicClassName);
        } catch (NullPointerException e) {
            if (!exportModel.isContainsStaticDependencies() && !dynamicClassName.getPackageName().startsWith("pl.fhframework")) {
                exportModel.setContainsStaticDependencies(!dynamicClassName.getInnerClassName().isPresent() || !isRegisteredDynamicClass(dynamicClassName.getOuterClassName()));
            }
            return;
        }

        for (DynamicClassName clazz1 : metadata.getDependencies()) {
            if (!visitedClasses.contains(clazz1)) {
                this.getWithDependencies(clazz1, exportModel, visitedClasses);
            }
        }
    }

    /**
     * Returns file descriptor for a dynamic class.
     *
     * @param dynamicClassName class name to look for in repo
     * @return file descriptor for a specific dynamic metadata area, like DynamicClassMetadata.
     */
    public synchronized DynamicClassFileDescriptor getFileDescriptor(DynamicClassName dynamicClassName) {
        DynamicClassRepositoryEntry repositoryEntry = DYNAMIC_CLASSES.get(dynamicClassName);
        if (repositoryEntry == null) {
            throw new NullPointerException("Dynamic class " + dynamicClassName + " is not in the repository. Register it first.");
        }
        return repositoryEntry.getXmlFile();
    }

    /**
     * set if dynamic class is Valid.
     *
     * @param dynamicClassName class name to set
     */
    public boolean setValid(DynamicClassName dynamicClassName, boolean valid) {
        IClassInfo info = DYNAMIC_CLASSES.get(dynamicClassName);
        if (info != null) {
            ((DynamicClassRepositoryEntry) info).setValid(valid);
            return true;
        }
        return false;
    }

    public String getNextSimpleClassName(DynamicClassName dnc) {
        if (DYNAMIC_CLASSES.containsKey(dnc)) {
            return getNextSimpleClassName(DYNAMIC_CLASSES.get(dnc));
        }
        return dnc.getBaseClassName() + "_V1";
    }

    public Set<IClassInfo> getRegistered(String module) {
        Subsystem subsystem = ModuleRegistry.getByName(module);

        Set<IClassInfo> allRegistered = new HashSet<>();
        allRegistered.addAll(DYNAMIC_CLASSES.values().stream().filter(entry -> entry.getSubsystem() == subsystem).collect(Collectors.toList()));
        allRegistered.addAll(STATIC_CLASSES.values().stream().filter(entry -> entry.getSubsystem() == subsystem).collect(Collectors.toList()));

        return allRegistered;
    }

    private void generateAndCompileClasses(Map<AbstractDynamicClassAreaHandler, List<DynamicClassName>> classesToCompileMap) {
        // generate java files
        Map<AbstractDynamicClassAreaHandler, Map<DynamicClassRepositoryEntry, Path>> generatedJavaFilesMap = new LinkedHashMap<>();
        List<DynamicClassName> allClassesToCompile = classesToCompileMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        classesToCompileMap.forEach((handler, classesToCompile) -> {
            Map<DynamicClassRepositoryEntry, Path> generatedJavaFiles = new LinkedHashMap<>();
            for (DynamicClassName classToCompile : classesToCompile) {
                DynamicClassRepositoryEntry classRepositoryEntry = DYNAMIC_CLASSES.get(classToCompile);
                generatedJavaFiles.put(classRepositoryEntry, generateClass(classToCompile, allClassesToCompile));
            }
            generatedJavaFilesMap.put(handler, generatedJavaFiles);
        });

        // compile all generated files
        dynamicClassCompiler.compile(
                generatedJavaFilesMap.values().stream().flatMap(handlerMap -> handlerMap.values().stream()).collect(Collectors.toList()),
                generatedJavaFilesMap.keySet().stream().filter(handel -> handel.getSupportedArea().isAspectWeavingNeeded()).count() > 0);

        // do post-compile
        generatedJavaFilesMap.forEach((handler, generatedJavaFiles) -> {
            generatedJavaFiles.forEach((repositoryEntry, javaFilePath) -> {
                DynamicClassMetadata metadata = repositoryEntry.metadata;
                handler.postCompile(metadata,
                        dynamicClassCompiler.getWorkingDirectoryPath(), javaFilePath,
                        metadata.getDynamicClassName().getPackageName(), getNextSimpleClassName(repositoryEntry));
            });
        });

        // load and do post-load
        generatedJavaFilesMap.forEach((handler, generatedJavaFiles) -> {
            generatedJavaFiles.forEach((repositoryEntry, javaFilePath) -> {
                DynamicClassMetadata metadata = repositoryEntry.metadata;
                // load class
                Class<?> clazz = dynamicClassCompiler.loadDynamicClass(
                        metadata.getDynamicClassName().getPackageName(), getNextSimpleClassName(repositoryEntry));

                // do post-load
                handler.postLoad(repositoryEntry.xmlFile, clazz, repositoryEntry.metadata);

                // change cache entry
                repositoryEntry.cachedClass = clazz;
                repositoryEntry.cachedClassAndDepencenciesXMLTimestamps = DependencyTimestampJavaGenerator.getXmlTimestamps(clazz);
                repositoryEntry.nextVersion++;
            });
        });

        // do post-load
        generatedJavaFilesMap.keySet().forEach(AbstractDynamicClassAreaHandler::postLoad);
    }

    private void resolveDependencies(DependenciesContext dependenciesContext, Collection<DynamicClassName> dependencies,
                                     Collection<DynamicClassName> pendingClasses,
                                     boolean checkAndCompile, boolean rootResolve) {
        Set<DynamicClassName> additionalDependencies = new HashSet<>();

        // resolve dependencies
        for (DynamicClassName dependency : dependencies) {
            // split into outer and optional inner class
            DynamicClassName dependencyOuterClass = dependency.getOuterClassName();
            Optional<String> dependencyInnerClass = dependency.getInnerClassName();

            // optionally check modifications and compile new class version
            if (checkAndCompile && isRegisteredDynamicClass(dependencyOuterClass)) {
                getOrCompileDynamicClass(dependencyOuterClass);
            }

            DynamicClassRepositoryEntry dependencyRepositoryEntry = DYNAMIC_CLASSES.get(dependencyOuterClass);
            DependencyResolution resolution;
            if (dependencyRepositoryEntry == null
                    // check for static inner classes of a dynamic classes static base class
                    // e.g. XxxForm is static base of dynamic class and XxxForm$SomeModel is a static inner class of this class XxxForm
                    || isInnerClassOfStaticClass(dependency)) {
                // non-dynamic class
                resolution = DependencyResolution.ofNonDynamicClass(dependency);
            } else if (pendingClasses.contains(dependencyOuterClass)) {
                // dynamic not yet ready (pending) class
                String dependencyNewClassName = getNextSimpleClassName(dependencyRepositoryEntry);
                if (dependencyInnerClass.isPresent()) {
                    dependencyNewClassName += "$" + dependencyInnerClass.get();
                }
                resolution = DependencyResolution.ofDynamicPendingClass(dependency.getPackageName(),
                        dependencyNewClassName, dependencyRepositoryEntry.metadata);
            } else {
                Class<?> readyClass = getOptionalInnerClass(dependencyRepositoryEntry.cachedClass, dependencyInnerClass);
                // dynamic already compiled class
                resolution = DependencyResolution.ofDynamicReadyClass(
                        readyClass, dependencyRepositoryEntry.metadata);
            }
            dependenciesContext.putResulotion(dependency, resolution);

            // rules can depends on types that are related to main dependent type, UC on Form internal model, UC on external UC params
            if (rootResolve && resolution.isDynamicClass()) {
                additionalDependencies.addAll(resolution.getMetadata().getDependencies());
            }
        }

        if (!additionalDependencies.isEmpty()) {
            additionalDependencies.removeAll(dependenciesContext.listDependencies());
            resolveDependencies(dependenciesContext, additionalDependencies, pendingClasses, checkAndCompile, false);
        }
    }

    private Path generateClass(DynamicClassName className, List<DynamicClassName> otherClassesToCompile) {
        try {
            DynamicClassRepositoryEntry repositoryEntry = DYNAMIC_CLASSES.get(className);
            DynamicClassMetadata metadata = repositoryEntry.getMetadata();
            // assign new class package and name
            DynamicClassName dynamicClassName = metadata.getDynamicClassName();
            String newClassName = getNextSimpleClassName(repositoryEntry);
            String newClassPackage = dynamicClassName.getPackageName();

            // resolve dependencies
            DependenciesContext dependenciesContext = new DependenciesContext();
            Set<DynamicClassName> dependencies = new HashSet<>(metadata.getDependencies());
            dependencies.add(className);
            resolveDependencies(dependenciesContext, dependencies, otherClassesToCompile, false, true);

            // generate class
            String generatedClassContent = getAreaHandler(repositoryEntry.getArea()).generateClass(
                    repositoryEntry.getMetadata(),
                    newClassPackage, newClassName,
                    DependencyTimestampJavaGenerator.generateStaticJavaMethod(collectDependencyMetadataTimestamps(className)),
                    dependenciesContext);

            // save to java file
            return dynamicClassCompiler.createDynamicJavaFile(generatedClassContent, newClassPackage, newClassName);
        } catch (FhFormGeneratorException e) {
            throw new FhDescribedNstException(FhLogger.resolveThrowableMessage(e, false));
        } catch (Exception e) {
            FhLogger.error("Error generating java code for dynamic artifact {}", className.toFullClassName(), e);
            throw new FhException(String.format("Error generating java code for dynamic artifact '%s'", className.toFullClassName()), e);
        }
    }

    /**
     * If class cache entry does not exist, build one based on available classes both Precompiled or compiled at runtime.
     * Creates cache entry with:
     * <ul>
     * <li>xmlFileURL set to url from provided repository key</li>
     * <li>nextVersion set to max of available runtime compiled classes plus one</li>
     * <li>cachedClass and cachedClassXMLTimestamp set to the latest class found (either a precompiled class or a runtime compiled with the highest version)</li>
     * </ul>
     */
    private void fillRepositoryEntryFromExistingClasses(DynamicClassRepositoryEntry repositoryEntry, DynamicClassName dynamicClassName) {
        Map<DynamicClassName, Instant> precompiledXmlTimestamp = null;

        // look for precompiled class in classpath
        try {
            Class<? extends Form> precompiledClass = (Class<? extends Form>) FhCL.classLoader.loadClass(
                    dynamicClassName.getPackageName() + "." + dynamicClassName.getBaseClassName() + PRECOMPILED_CLASS_SUFFIX);

            // build repository key
            precompiledXmlTimestamp = DependencyTimestampJavaGenerator.getXmlTimestamps(precompiledClass);
            repositoryEntry.cachedClass = precompiledClass;
            repositoryEntry.cachedClassAndDepencenciesXMLTimestamps = precompiledXmlTimestamp;
            // post load
            getAreaHandler(repositoryEntry.getArea()).postLoad(repositoryEntry.getXmlFile(), precompiledClass, repositoryEntry.getMetadata());

        } catch (ClassNotFoundException ignored) {
            // no precompiled class available
        }

        // look for runtime compiled classes in dynamic classes directory
        Path targetPackageDirectory = dynamicClassCompiler.getWorkingDirectoryPath().resolve(
                dynamicClassName.getPackageName().replace('.', File.separatorChar));
        if (Files.exists(targetPackageDirectory)) {
            int latestRuntimeCompiledVersion = -1;
            Path latestRuntimeCompiledClassPath = null;
            Pattern pattern = Pattern.compile(dynamicClassName.getBaseClassName() + "_V(\\d+)\\.class");
            try {
                List<Path> filesInPackageDirectory = Files.walk(targetPackageDirectory).filter(
                        path -> path.getParent().equals(targetPackageDirectory)).collect(Collectors.toList());
                for (Path file : filesInPackageDirectory) {
                    // extract version from class name
                    Matcher nameMatcher = pattern.matcher(file.getFileName().toString());
                    if (nameMatcher.find()) {
                        try {
                            int version = Integer.parseInt(nameMatcher.group(1));
                            // if version if higher - use this file
                            if (version > latestRuntimeCompiledVersion) {
                                latestRuntimeCompiledVersion = version;
                                latestRuntimeCompiledClassPath = file;
                            }
                        } catch (NumberFormatException ignored) {
                            // just ignore this file
                        }
                    }
                }
            } catch (IOException e) {
                throw new FhFormException("Exception while listing files of " + targetPackageDirectory.toString(), e);
            }

            if (latestRuntimeCompiledClassPath != null) {
                // build repository key if runtime compiled class if newer than the precompiled one (if present)
                try {
                    // set version even if eventually will be using the precompiled class
                    repositoryEntry.nextVersion = latestRuntimeCompiledVersion + 1;

                    // load dynamic class
                    Class<? extends Form> dynamicClass = (Class<? extends Form>) dynamicClassCompiler.loadDynamicClass(
                            dynamicClassName.getPackageName(), dynamicClassName.getBaseClassName() + "_V" + latestRuntimeCompiledVersion);
                    Map<DynamicClassName, Instant> dynamicTimestamps = DependencyTimestampJavaGenerator.getXmlTimestamps(dynamicClass);

                    // compare to the precompiled one (if present)
                    if (precompiledXmlTimestamp == null || isBefore(precompiledXmlTimestamp, dynamicTimestamps)) {
                        // do post-load
                        getAreaHandler(repositoryEntry.getArea()).postLoad(repositoryEntry.xmlFile, dynamicClass, repositoryEntry.metadata);

                        // use runtime compiled class
                        repositoryEntry.cachedClassAndDepencenciesXMLTimestamps = dynamicTimestamps;
                        repositoryEntry.cachedClass = dynamicClass;
                        FhLogger.info(this.getClass(), "Found already compiled form class {}", repositoryEntry.cachedClass.getName());
                    }
                } catch (Exception e) {
                    FhLogger.warn("Error loading old compiled class {} from dynamic classes directory. Message: {}", dynamicClassName.toFullClassName(), e.getMessage());
                }
            }
        }
    }

    private void collectDependencyMetadataTimestampsImpl(DynamicClassName className, Map<DynamicClassName, Instant> timestamps) {
        DynamicClassName outterClassName = className.getOuterClassName();
        DynamicClassRepositoryEntry<?> repositoryEntry = DYNAMIC_CLASSES.get(outterClassName);

        // check for static inner classes of a dynamic classes static base class
        // e.g. XxxForm is static base of dynamic class and XxxForm$SomeModel is a static inner class of this class XxxForm
        if (isInnerClassOfStaticClass(className)) {
            timestamps.put(className, null);
        } else if (repositoryEntry != null) {
            timestamps.put(outterClassName, repositoryEntry.getMetadataXMLTimestamp());
            for (DynamicClassName dependency : repositoryEntry.getMetadata().getDependencies()) {
                DynamicClassName depTimestampClass = isInnerClassOfStaticClass(dependency) ? dependency : dependency.getOuterClassName();
                if (!timestamps.containsKey(depTimestampClass)) {
                    collectDependencyMetadataTimestampsImpl(dependency, timestamps);
                }
            }
        } else {
            timestamps.put(outterClassName, null);
        }
    }

    private Map<DynamicClassName, Instant> collectDependencyMetadataTimestamps(DynamicClassName className) {
        Map<DynamicClassName, Instant> timestamps = new HashMap<>();
        collectDependencyMetadataTimestampsImpl(className, timestamps);
        return timestamps;
    }

    private boolean isBefore(Map<DynamicClassName, Instant> timestamps, Map<DynamicClassName, Instant> otherTimestamps) {
        Instant maxTimestamp = getMax(timestamps.values());
        Instant maxOtherTimestamp = getMax(otherTimestamps.values());
        return (maxTimestamp == null && otherTimestamps != null) || (maxTimestamp != null && maxOtherTimestamp != null && maxTimestamp.isBefore(maxOtherTimestamp));
    }

    private Instant getMax(Collection<Instant> timestamps) {
        Instant max = null;
        for (Instant timestamp : timestamps) {
            if (max == null || (timestamp != null && timestamp.isAfter(max))) {
                max = timestamp;
            }
        }
        return max;
    }

    private Map<DynamicClassArea, List<DynamicClassName>> collectClassesForRecompilation(DynamicClassName rootClass) {
        LinkedMultiValueMap<DynamicClassArea, DynamicClassName> compilationAreaMap = new LinkedMultiValueMap();
        Set<DynamicClassName> alreadyVisited = new LinkedHashSet<>();
        collectClassesForRecompilationImpl(rootClass, compilationAreaMap, alreadyVisited);
        return compilationAreaMap;
    }

    /**
     * Collects classes for recompilation
     *
     * @param classToCheck       class to be checked
     * @param compilationAreaMap area -> classes to compile map
     * @param alreadyVisited     already visited classes
     * @return true if class needs compilation
     */
    private void collectClassesForRecompilationImpl(DynamicClassName classToCheck,
                                                    LinkedMultiValueMap<DynamicClassArea, DynamicClassName> compilationAreaMap,
                                                    Set<DynamicClassName> alreadyVisited) {
        // avoid checking again and cyclic dependency loops
        if (alreadyVisited.contains(classToCheck)) {
            return;
        }

        alreadyVisited.add(classToCheck);

        DynamicClassRepositoryEntry<?> repositoryEntry = DYNAMIC_CLASSES.get(classToCheck);
        if (repositoryEntry == null) {
            // not a dynamic class - just skip it
            return;
        }

        // refesh from XML if needed
        refreshMetadataIfNeeded(repositoryEntry);

        // check if recompilation needed
        if (didTimestampsChange(classToCheck, repositoryEntry, true)) {
            Optional<Class<?>> readyClass = getAreaHandler(repositoryEntry.getArea()).getReadyClass(repositoryEntry.getMetadata());
            if (readyClass.isPresent()) {
                repositoryEntry.cachedClass = readyClass.get();
                repositoryEntry.cachedClassAndDepencenciesXMLTimestamps = new HashMap<DynamicClassName, Instant>()  {
                    {
                        put(repositoryEntry.getClassName(), repositoryEntry.getMetadataXMLTimestamp());
                    }
                };
            }
            else {
                // add to proper area
                compilationAreaMap.add(repositoryEntry.getArea(), classToCheck);
            }
        }

        // collect from dependencies also
        for (DynamicClassName dependency : repositoryEntry.getMetadata().getDependencies()) {
            collectClassesForRecompilationImpl(dependency, compilationAreaMap, alreadyVisited);
        }
    }

    private boolean didTimestampsChange(DynamicClassName className, DynamicClassRepositoryEntry<?> repositoryEntry, boolean logInfo) {
        if (repositoryEntry == null) {
            // no entry = static class
            return false;
        }
        if (repositoryEntry.cachedClassAndDepencenciesXMLTimestamps == null) {
            if (logInfo) {
                FhLogger.info(this.getClass(), "Dynamic class {} XML file was never compiled.", className);
            }
            return true;
        }
        // dependecy XML timestamps (includes this class own file so will check own file also)
        for (Map.Entry<DynamicClassName, Instant> dependencyXMLTimestamp : repositoryEntry.cachedClassAndDepencenciesXMLTimestamps.entrySet()) {
            DynamicClassName dependencyClassName = dependencyXMLTimestamp.getKey();
            DynamicClassRepositoryEntry dependencyEntry = DYNAMIC_CLASSES.get(dependencyClassName);
            Instant cachedXmlTimestamp = dependencyXMLTimestamp.getValue();
            Instant currentXmlTimestamp = null;
            if (dependencyEntry != null) {
                refreshXMLTimestamp(dependencyEntry, false);
                currentXmlTimestamp = dependencyEntry.lastKnownXMLTimestamp;
            }
            // one or both timestamps may be null when class is/was static
            if (!Objects.equals(cachedXmlTimestamp, currentXmlTimestamp)) {
                // ZIP (JAR) loss of timestamp precision workaround
                boolean needsJarWorkaround = cachedXmlTimestamp != null && currentXmlTimestamp != null &&
                        currentXmlTimestamp.getNano() == 0 &&
                        cachedXmlTimestamp.getEpochSecond() / 2 == currentXmlTimestamp.getEpochSecond() / 2;
                if (!needsJarWorkaround) {
                    if (logInfo) {
                        FhLogger.info(this.getClass(), "Dynamic class {} dependency class {} XML file has been changed since last compilation. " +
                                        "Previous timestamp {}, current timestamp {}.",
                                className, dependencyClassName,
                                cachedXmlTimestamp != null ? TIMESTAMP_FORMATTER.format(cachedXmlTimestamp) : "<no XML>",
                                currentXmlTimestamp != null ? TIMESTAMP_FORMATTER.format(currentXmlTimestamp) : "<no XML>");
                    }

                    return true;
                }
            }
        }
        // todo: for XSD
        if (repositoryEntry.cachedClassAndDepencenciesXMLTimestamps.isEmpty()) {
            return !Objects.equals(repositoryEntry.lastKnownXMLTimestamp, repositoryEntry.metadataXMLTimestamp);
        }
        return false;
    }

    private DynamicClassMetadata refreshMetadataIfNeeded(DynamicClassRepositoryEntry repositoryEntry) {
        try {
            SnapshotsModelAspect.turnOff();
            AbstractDynamicClassAreaHandler handler = getAreaHandler(repositoryEntry.getArea());
            repositoryEntry.lastKnownXMLTimestamp = repositoryEntry.metadataXMLTimestamp == null ? null : handler.getLastKnownTimestamp(repositoryEntry.getXmlFile(), repositoryEntry.getMetadata());
            repositoryEntry.lastXMLChangeCheckTimestamp = Instant.now();
            if (repositoryEntry.metadataXMLTimestamp == null
                    || !repositoryEntry.metadataXMLTimestamp.equals(repositoryEntry.lastKnownXMLTimestamp)) {
                DynamicClassMetadata oldMetadata = repositoryEntry.getMetadata();

                // recreate metadata from XML file
                DynamicClassMetadata metadata = handler.readMetadata(repositoryEntry.getXmlFile());

                if (oldMetadata != null) {
                    // check if name changed
                    if (!oldMetadata.getDynamicClassName().equals(metadata.getDynamicClassName())) {
                        throw new FhException("Dynamic class name not supported yet. Detected change from "
                                + oldMetadata.getDynamicClassName() + " to " + metadata.getDynamicClassName());
                    }

                    // remove from old dependant classes
                    for (DynamicClassName oldDependency : oldMetadata.getDependencies()) {
                        getDependantClassesSet(oldDependency).remove(metadata.getDynamicClassName());
                    }
                }

                // add to new dependant classes
                for (DynamicClassName newDependency : metadata.getDependencies()) {
                    getDependantClassesSet(newDependency).add(metadata.getDynamicClassName());
                }

                // update metadata in repo
                repositoryEntry.metadata = metadata;
                if (repositoryEntry.metadataXMLTimestamp == null) {
                    repositoryEntry.lastKnownXMLTimestamp = handler.getLastKnownTimestamp(repositoryEntry.getXmlFile(), repositoryEntry.getMetadata());
                }
                repositoryEntry.metadataXMLTimestamp = repositoryEntry.lastKnownXMLTimestamp;
            }
            return repositoryEntry.metadata;
        } finally {
            SnapshotsModelAspect.turnOn();
        }
    }

    /**
     * Checks XML file modification timestmp, but only if not checked recently
     */
    private void refreshXMLTimestamp(DynamicClassRepositoryEntry repositoryEntry, boolean forceCheck) {
        if (repositoryEntry.lastXMLChangeCheckTimestamp == null
                || Instant.now().isAfter(repositoryEntry.lastXMLChangeCheckTimestamp.plusMillis(MAX_FILE_CHECK_FREQUNECY))) {
            AbstractDynamicClassAreaHandler handler = getAreaHandler(repositoryEntry.getArea());
            repositoryEntry.lastKnownXMLTimestamp = handler.getLastKnownTimestamp(repositoryEntry.getXmlFile(), repositoryEntry.getMetadata());
            repositoryEntry.lastXMLChangeCheckTimestamp = Instant.now();
        }
    }

    /**
     * Resolves full form file URL based on optionally provided subsystem, a relative form file path and a form class.
     * <br>The base URL is taken from (in this order):
     * <ul>
     * <li>the provided subsystem if present</li>
     * <li>subsystem which is the owner of a provided form class</li>
     * <li>base URL </li>
     * </ul>
     *
     * @param subsystem    subsystem (optional)
     * @param formFilePath relavite XML form file's path
     * @param formClass    form class
     * @return
     */
    private URL resolveFormURL(Optional<Subsystem> subsystem, String formFilePath, Class<? extends Form> formClass) {
        // if subsystem is not explicitly set try to find subsystem which ownes this form class
        if (!subsystem.isPresent()) {
            subsystem = ModuleRegistry.findOwningSubsystem(formClass);
        }

        FhResource baseUrl;
        if (subsystem.isPresent()) {
            baseUrl = (subsystem.get().getBasePath());
        } else {
            baseUrl = ReflectionUtils.baseClassPath(formClass);
        }
        return FileUtils.resolve(baseUrl.getURL(), formFilePath);
    }

    private String getNextSimpleClassName(DynamicClassRepositoryEntry entry) {
        return entry.metadata.getDynamicClassName().getBaseClassName() + "_V" + entry.nextVersion;
    }

    private Set<DynamicClassName> getDependantClassesSet(DynamicClassName dynamicClassName) {
        if (!DYNAMIC_CLASS_DEPENDANT_CLASSES.containsKey(dynamicClassName)) {
            DYNAMIC_CLASS_DEPENDANT_CLASSES.put(dynamicClassName, new HashSet<>());
        }
        return DYNAMIC_CLASS_DEPENDANT_CLASSES.get(dynamicClassName);
    }

    private DynamicClassArea getClassArea(DynamicClassName dynamicClassName) {
        DynamicClassRepositoryEntry<?> entry = DYNAMIC_CLASSES.get(dynamicClassName);
        return entry != null ? entry.getArea() : null;
    }

    protected Class<?> getOptionalInnerClass(Class<?> readyClass, Optional<String> innerClassName) {
        if (innerClassName.isPresent()) {
            String fullInnerClassName = readyClass.getName() + "$" + innerClassName.get();
            return classForName(fullInnerClassName, true);
        } else {
            return readyClass;
        }
    }

    protected Class<?> classForName(String fullInnerClassName, boolean required) {
        return dynamicClassCompiler.classForName(fullInnerClassName, required);
    }

    private boolean containsDynamicClassIgnoreCase(Set<DynamicClassName> classNameList, DynamicClassName newClass) {
        for (DynamicClassName dynamicClassName : classNameList) {
            if (newClass.toFullClassName().equalsIgnoreCase(dynamicClassName.toFullClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if static given class is inner class of a static class.
     */
    private boolean isInnerClassOfStaticClass(DynamicClassName dynamicClassName) {
        Class clazz = classForName(dynamicClassName.toFullClassName(), false);
        return dynamicClassName.getInnerClassName().isPresent() && clazz != null && !AdHocForm.class.isAssignableFrom(clazz.getEnclosingClass());
    }
}
