package pl.fhframework.compiler.core.dynamic;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyTimestampJavaGenerator;
import pl.fhframework.compiler.core.generator.DynamicClassCompiler;
import pl.fhframework.compiler.core.generator.EnumsTypeProvider;
import pl.fhframework.compiler.core.generator.JavaCodeFromXsdService;
import pl.fhframework.compiler.core.model.ClassModelReferenceFactory;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.model.DynamicModelXmlManager;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.rules.service.RulesServiceExt;
import pl.fhframework.compiler.core.rules.service.RulesServiceExtImpl;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.compiler.core.services.service.FhServicesServiceExtImpl;
import pl.fhframework.compiler.core.uc.dynamic.model.DynamicUseCaseManager;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.compiler.core.uc.service.UseCaseService;
import pl.fhframework.compiler.core.uc.service.UseCaseServiceImpl;
import pl.fhframework.compiler.forms.FormsManager;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhException;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.generator.GenericExpressionConverter;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.core.services.service.FhServicesService;
import pl.fhframework.core.services.service.FhServicesServiceImpl;
import pl.fhframework.core.util.DebugUtils;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.forms.FormsUtils;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.Form;
import pl.fhframework.subsystems.ArtificialSubsystem;
import pl.fhframework.subsystems.Subsystem;
import pl.fhframework.tools.loading.FormReader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Maven dynamic class compiler. Manages generation and compilation of dynamic classes in Maven.
 */
public class MavenDynamicClassCompiler {
    public static final String RESET = "\033[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ERROR = "[" + ANSI_RED + "ERROR" + RESET + "] ";

    private static final Logger LOGGER = LoggerFactory.getLogger(MavenDynamicClassCompiler.class);

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault()).withLocale(Locale.getDefault());

    private static class LocalDynamicClassInfo {

        private DynamicClassArea area;

        private DynamicClassName dynamicClassName;

        private String newClassName;

        private String fullTargetClassName;

        private Path javaFilePath;

        private Instant javaFileTimestamp;

        private Class<?> precompiledClass;

        private Map<DynamicClassName, Instant> precompiledClassXMLTimestamps;

        private DynamicClassFileDescriptor xmlFilePath;

        private Instant xmlTimestamp;

        private DynamicClassMetadata metadata;

        private boolean needsCompilation;

        private Throwable error;

        public DynamicClassMetadata getMetadata(Map<DynamicClassArea, AbstractDynamicClassAreaHandler> areaHandlers) {
            if (metadata == null) {
                metadata = areaHandlers.get(area).readMetadata(xmlFilePath);
            }
            return metadata;
        }
    }

    private final Map<DynamicClassArea, AbstractDynamicClassAreaHandler> AREA_HANDLERS = new HashMap<>();

    private Map<DynamicClassName, LocalDynamicClassInfo> localDynamicClassesByClassName = new HashMap<>();

    private Map<DynamicClassArea, List<LocalDynamicClassInfo>> localDynamicClassesByArea = new HashMap<>();

    private Map<DynamicClassName, Instant> dynamicClassesXmlTimestampsCache = new HashMap<>();

    private DynamicClassCompiler dynamicClassCompiler;

    private Path sourcesPath;

    private Path outputPath;

    private Path classesPath;

    private boolean failOnError;

    private ClassLoader temporaryClassLoader;

    private MavenAutowireFactory autowireFactory;

    public static void main(String[] argStrings) throws Exception {
        SimpleCommandLinePropertySource args = new SimpleCommandLinePropertySource(argStrings);
        String sources = args.getProperty("sources");
        String output = args.getProperty("output");
        String classes = args.getProperty("classes");
        boolean failOnError = true;
        if (args.containsProperty("failOnError")) {
            failOnError = !"false".equals(args.getProperty("failOnError").toLowerCase());
        }
        boolean verbose = true;
        if (args.containsProperty("verbose")) {
            verbose = !"false".equals(args.getProperty("verbose").toLowerCase());
        }
        DynamicClassCompiler.setCoreLiteTarget(StringUtils.equalsIgnoreCase("true", args.getProperty("coreLiteTarget")));
        // suppress unnecessary logs
        suppressInternalLogs();

        // set logger level
        configureLogger(LOGGER, verbose ? Level.DEBUG : Level.INFO);

        new MavenDynamicClassCompiler(sources, output, classes, failOnError).run();
    }

    public MavenDynamicClassCompiler(String sources, String output, String classes, boolean failOnError) throws Exception {
        FhCL.init(this.getClass().getClassLoader());
        this.sourcesPath = Paths.get(sources);
        this.outputPath = Paths.get(output);
        this.classesPath = Paths.get(classes).toAbsolutePath();

        this.failOnError = failOnError;

        dynamicClassCompiler = new DynamicClassCompiler(classesPath, FhCL.classLoader);
        dynamicClassCompiler.setClassloaderUrlsBasedClassPath(true);

        initAutowireFactory();
        initPluginableCodeGenerator();
        AREA_HANDLERS.put(DynamicClassArea.FORM, autowireFactory.getBean(FormsManager.class));
        AREA_HANDLERS.put(DynamicClassArea.MODEL, autowireFactory.getBean(DynamicModelManager.class));
        AREA_HANDLERS.put(DynamicClassArea.USE_CASE, autowireFactory.getBean(DynamicUseCaseManager.class));
        AREA_HANDLERS.put(DynamicClassArea.RULE, autowireFactory.getBean(DynamicRuleManager.class));
        AREA_HANDLERS.put(DynamicClassArea.SERVICE, autowireFactory.getBean(DynamicFhServiceManager.class));

        // create classloader
        URL url = classesPath.toAbsolutePath().toUri().toURL();
        temporaryClassLoader = FhCL.classLoader;
        ((FhCL) temporaryClassLoader).addURL(url);

    }

    private void initPluginableCodeGenerator() {
        // TODO: LATER make it discover class at runtime
        Arrays.asList(
                ReflectionUtils.tryGetClassForName("pl.fhframework.compiler.core.services.dynamic.generator.CoreElementsRegister"),
                ReflectionUtils.tryGetClassForName("pl.fhframework.compiler.core.integration.IntegrationElementsRegister")
                ).forEach(codeGeneratorRegister -> {
            if (codeGeneratorRegister != null) {
                execRegister(codeGeneratorRegister);
            }
        });
    }

    private void execRegister(Class<?> codeGeneratorRegister) {
        Optional<Method> method = ReflectionUtils.findMatchingPublicMethod(codeGeneratorRegister, "onSubsystemStart", Subsystem.class);
        ReflectionUtils.run(method.get(), ReflectionUtils.createClassObject(codeGeneratorRegister), new Object[]{null});
    }

    private void initAutowireFactory() {
        autowireFactory = new MavenAutowireFactory();
        AutowireHelper.setAutowireFactory(autowireFactory);

        autowireFactory.putBean(dynamicClassCompiler, DynamicClassCompiler.class);

        autowireFactory.putBean(new GenericExpressionConverter(), GenericExpressionConverter.class);
        autowireFactory.putBean(new DynamicRuleManager(), DynamicRuleManager.class);
        autowireFactory.putBean(new DynamicFhServiceManager(), DynamicFhServiceManager.class);
        autowireFactory.putBean(new RulesServiceExtImpl(), RulesServiceExtImpl.class, RulesService.class, RulesServiceExt.class);

        autowireFactory.putBean(new FhServicesServiceExtImpl(), FhServicesServiceExtImpl.class, FhServicesService.class, FhServicesServiceImpl.class);

        autowireFactory.putBean(new DynamicUseCaseManager(), DynamicUseCaseManager.class);
        autowireFactory.putBean(new UseCaseServiceImpl(), UseCaseServiceImpl.class, UseCaseService.class);
        autowireFactory.putBean(new UseCaseModelUtils(), UseCaseModelUtils.class);

        autowireFactory.putBean(new DynamicModelManager(), DynamicModelManager.class);
        autowireFactory.putBean(new DynamicModelXmlManager(), DynamicModelXmlManager.class);
        autowireFactory.putBean(new ClassModelReferenceFactory(), ClassModelReferenceFactory.class);
        autowireFactory.putBean(new EnumsTypeProvider(), EnumsTypeProvider.class);

        autowireFactory.putBean((JavaCodeFromXsdService.ClassNameResolver)
                (packageName, className) -> addPrecompiled(className, DynamicClassName.forClassName(packageName, className).toFullClassName(), DynamicClassArea.MODEL),
                JavaCodeFromXsdService.ClassNameResolver.class);

        autowireFactory.putBean(new FormsManager(), FormsManager.class);

        autowireFactory.crossWireBeans();
    }

    private void run() {
        LOGGER.info("{} started on {}", MavenDynamicClassCompiler.class.getSimpleName(), toString(Instant.now()));

        for (DynamicClassArea area : DynamicClassArea.values()) {
            if (area.isPrecompile() && AREA_HANDLERS.containsKey(area)) {
                buildStateForArea(AREA_HANDLERS.get(area), area);
            }
        }
        for (DynamicClassArea[] areas : DynamicClassRepository.AREA_COMPILATION_ORDER) {
            if (Arrays.stream(areas).anyMatch(DynamicClassArea::isPrecompile)) {
                for (DynamicClassArea area : areas) {
                    if (area.isPrecompile() && AREA_HANDLERS.containsKey(area)) {
                        checkNeedsCompilationForArea(area);
                    }
                }
                compileForArea(areas);
            }
        }
    }

    private void buildStateForArea(AbstractDynamicClassAreaHandler areaHandler, DynamicClassArea area) {

        ArtificialSubsystem subsystem = new ArtificialSubsystem("__maven__", FhResource.get(sourcesPath));

        List<LocalDynamicClassInfo> areaLocalClassList = new ArrayList<>();
        localDynamicClassesByArea.put(area, areaLocalClassList);


        // find all form files
        List<Path> foundFormFiles = findXmlFiles(sourcesPath, areaHandler.getXmlFilenameExtension());

        LOGGER.info("Found {} {} XML files in {}", area.name(), foundFormFiles.size(), sourcesPath.toString());

        // process each file
        for (Path xmlFile : foundFormFiles) {
            LOGGER.debug("Reading state of {}", xmlFile.toString());
            LocalDynamicClassInfo classInfo = new LocalDynamicClassInfo();
            try {
                // deduce package and class name from file name and directory
                String formRelativePath = sourcesPath.relativize(xmlFile).toString();
                DynamicClassName dynamicClassName;
                if (area == DynamicClassArea.FORM) {
                    // special case of composite forms
                    dynamicClassName = FormsManager.getDynamicClassName(formRelativePath);
                } else {
                    dynamicClassName = DynamicClassName.forXmlFile(formRelativePath, areaHandler.getXmlFilenameExtension());
                }
                String packageName = dynamicClassName.getPackageName();
                String classTargetName = addPrecompiled(dynamicClassName.getBaseClassName(), dynamicClassName.toFullClassName(), area);

                // check if generated java file exists and is up to date
                Path javaFilePath = outputPath
                        .resolve(packageName.replace('.', File.separatorChar))
                        .resolve(classTargetName + ".java");

                classInfo.xmlFilePath = DynamicClassFileDescriptor.forPath(xmlFile, formRelativePath, subsystem);
                classInfo.xmlTimestamp = FileUtils.getLastModified(xmlFile);
                classInfo.dynamicClassName = dynamicClassName;
                classInfo.area = area;
                classInfo.javaFilePath = javaFilePath;
                if (Files.exists(javaFilePath)) {
                    classInfo.javaFileTimestamp = FileUtils.getLastModified(javaFilePath);
                }
                classInfo.fullTargetClassName = packageName + "." + classTargetName;

                readPrecompiledClassInfo(classInfo);

                Optional<Class<?>> readyClass = areaHandler.getReadyClass(classInfo.getMetadata(AREA_HANDLERS));
                if (readyClass.isPresent()) {
                    classInfo.precompiledClass = readyClass.get();
                    classInfo.needsCompilation = false;
                }
                else {
                    localDynamicClassesByClassName.put(dynamicClassName, classInfo);
                    // prefill timestamps' cache
                    dynamicClassesXmlTimestampsCache.put(dynamicClassName, classInfo.xmlTimestamp);
                    areaLocalClassList.add(classInfo);
                }
            } catch (Throwable e) {
                String msg = String.format("Error while reading state of %s", xmlFile.toString());
                if (failOnError) {
                    failOnError(msg, e);
                } else {
                    LOGGER.error(msg, e);
                    classInfo.error = e;
                }
            }
        }
    }

    private void failOnError(String msg, Throwable e) {
        if (e instanceof FhFormException || e instanceof FhBindingException) {
            StringBuilder sb = new StringBuilder("\n" + ERROR + msg + "\n");
            sb.append(ERROR + e.getMessage() + "\n");
            while (e.getCause() != null) {
                if (e instanceof FhException) {
                    sb.append(ERROR + e.getCause().getMessage() + "\n");
                }
                e = e.getCause();
            }
            LOGGER.error(sb.toString());
            throw new RuntimeException("", null, false, false) {
                @Override
                public synchronized Throwable fillInStackTrace() {
                    return this;
                }
            };
        }
        else {
            throw new FhException(msg, e);
        }
    }

    private void checkNeedsCompilationForArea(DynamicClassArea area) {
        for (LocalDynamicClassInfo classInfo : localDynamicClassesByArea.get(area)) {
            classInfo.needsCompilation = checkNeedsCompilationForClass(classInfo, area);
        }
    }

    private boolean checkNeedsCompilationForClass(LocalDynamicClassInfo classInfo,
                                                  DynamicClassArea dependantArea) {
        // already checked
        if (classInfo.needsCompilation) {
            return true;
        }
        // java file not present
        if (classInfo.javaFileTimestamp == null) {
            return true;
        }
        // precompiled file not present
        if (classInfo.precompiledClass == null) {
            return true;
        }

        if (classInfo.precompiledClassXMLTimestamps == null) {
            return true;
        }

        // check all deps (timestamps in precompiled class include also all dependencies of all dependencies - the list is complete)
        for (Map.Entry<DynamicClassName, Instant> depEntry : classInfo.precompiledClassXMLTimestamps.entrySet()) {
            Instant precompiledTimestamp = depEntry.getValue();
            Instant currentTimestamp;
            try {
                currentTimestamp = getTimestampForDependencyClass(depEntry.getKey(), classInfo.dynamicClassName);
            } catch (RuntimeException e) {
                // at this moment the class wasn't found
                return true;
            }
            if (!Objects.equals(precompiledTimestamp, currentTimestamp)) {
                return true;
            }
        }

        // class is up-to-date and compilation is not needed
        return false;
    }

    /**
     * Checks current timestamp from: file timestamp for local XML (currently built module)
     * or timestamp from precompiled class (previously built module).
     * @return timestamp if dynamic class, null if static class
     * @throws RuntimeException if class not found at all
     */
    private Instant getTimestampForDependencyClass(DynamicClassName dependencyClass,
                                                   DynamicClassName dependantClass) {
        DynamicClassName dependencyOutterClass = dependencyClass.getOuterClassName();
        // try to use case first
        if (dynamicClassesXmlTimestampsCache.containsKey(dependencyOutterClass)) {
            return dynamicClassesXmlTimestampsCache.get(dependencyOutterClass);
        }
        Instant timestamp = getTimestampForDependencyClassNoCache(dependencyClass, dependencyOutterClass, dependantClass);
        dynamicClassesXmlTimestampsCache.put(dependencyOutterClass, timestamp);
        return timestamp;
    }

    private Instant getTimestampForDependencyClassNoCache(DynamicClassName dependencyClass,
                                                          DynamicClassName dependencyOutterClass,
                                                          DynamicClassName dependantClass) {
        // if is local (currently built module's) dynamic class
        if (localDynamicClassesByClassName.containsKey(dependencyOutterClass)) {
            return localDynamicClassesByClassName.get(dependencyOutterClass).xmlTimestamp;
        } else {
            Class<?> precompiledExternalClass = tryGetTemporaryClass(addPrecompiled(dependencyOutterClass.toFullClassName(), dependencyOutterClass.toFullClassName(), null));
            if (precompiledExternalClass != null) {
                try {
                    // get timestamps from this precompiled class and return timestamp of this class
                    return DependencyTimestampJavaGenerator.getXmlTimestamps(precompiledExternalClass).get(dependencyOutterClass);
                } catch (Exception e) {
                    String msg = "Cannot get timestamps of class " + precompiledExternalClass.getName();
                    LOGGER.error(msg, e);
                    throw new FhException(msg, e);
                }
            } else {
                try {
                    ReflectionUtils.getClassForName(dependencyClass.toFullClassName());

                    // static class was found - return null as it is static and has no XML timestamp
                    return null;
                } catch (Exception e) {
                    LOGGER.error("Cannot find {} dependency {}: {}",
                            dependantClass.toFullClassName(), dependencyClass.toFullClassName(), e.getMessage());
                    throw e;
                }
            }
        }
    }

    private void compileForArea(DynamicClassArea[] areas) {

        Map<LocalDynamicClassInfo, Path> generatedFiles = new HashMap<>();

        int countUpToDate = 0;
        List<LocalDynamicClassInfo> errors = new ArrayList<>();
        int countCompiled = 0;

        // generate
        for (DynamicClassArea area : areas) {
            AbstractDynamicClassAreaHandler areaHandler = AREA_HANDLERS.get(area);
            if (areaHandler == null) {
                continue;
            }

            for (LocalDynamicClassInfo classInfo : localDynamicClassesByArea.get(area)) {
                // skip if not needed
                if (!classInfo.needsCompilation) {
                    countUpToDate++;
                    continue;
                }
                if (classInfo.error != null) {
                    errors.add(classInfo);
                    continue;
                }

                try {
                    String newClassPackage = classInfo.dynamicClassName.getPackageName();
                    classInfo.newClassName = addPrecompiled(classInfo.dynamicClassName.getBaseClassName(), classInfo.dynamicClassName.toFullClassName(), area);
                    // read metadata
                    DynamicClassMetadata metadata = classInfo.getMetadata(AREA_HANDLERS);

                    // resolve dependencies
                    DependenciesContext dependenciesContext = new DependenciesContext();
                    resolveDependencies(metadata.getDependencies(), dependenciesContext, true);

                    // generate class
                    String generatedClassContent = areaHandler.generateClass(
                            metadata,
                            newClassPackage, classInfo.newClassName,
                            DynamicClassCompiler.isCoreLiteTarget() ? new GenerationContext() : DependencyTimestampJavaGenerator.generateStaticJavaMethod(collectDependencyMetadataTimestamps(classInfo.dynamicClassName)),
                            dependenciesContext);

                    // save to java file
                    Path javaFile = dynamicClassCompiler.createDynamicJavaFile(outputPath, generatedClassContent, newClassPackage, classInfo.newClassName);
                    generatedFiles.put(classInfo, javaFile);
                    countCompiled++;
                } catch (Throwable e) {
                    String msg = String.format("Error while processing %s (%s)", classInfo.dynamicClassName.toFullClassName(), classInfo.area.name());
                    if (failOnError) {
                        failOnError(msg, e);
                    } else {
                        LOGGER.error(msg, e);
                        classInfo.error = e;
                        errors.add(classInfo);
                    }
                }
            }
        }

        int errorCount = errors.size();

        // compile
        if (!generatedFiles.isEmpty()) {
            try {
                dynamicClassCompiler.compile(generatedFiles.values(), Arrays.stream(areas).filter(DynamicClassArea::isAspectWeavingNeeded).count() > 0);
                generatedFiles.forEach((classInfo, javaFile) -> {
                    DynamicClassMetadata metadata = classInfo.getMetadata(AREA_HANDLERS);
                    AREA_HANDLERS.get(classInfo.area).postCompile(metadata, dynamicClassCompiler.getWorkingDirectoryPath(), javaFile, classInfo.dynamicClassName.getPackageName(), classInfo.newClassName);
                });
            } catch (Throwable e) {
                errorCount = countCompiled;
                countCompiled = 0;
                if (failOnError) {
                    failOnError("Error while compiling generated classes", e);
                }
                // errors already logged by DynamicClassCompiler
            }
        }

        // mark as not needing compilation
        generatedFiles.keySet().forEach(classInfo -> {
            classInfo.needsCompilation = false;
        });

        // stats
        if (countUpToDate > 0 || countCompiled > 0 || errorCount > 0) {
            LOGGER.info("{} area summary: up-to-date {}, compiled {}, errors {}",
                    Arrays.stream(areas).map(DynamicClassArea::name).collect(Collectors.joining(", ")), countUpToDate, countCompiled, errorCount);
            if (!errors.isEmpty()) {
                LOGGER.error("ERRORS:");
                errors.forEach(classWithError -> {
                    Throwable cause = DebugUtils.getRootCause(classWithError.error);
                    LOGGER.error("    {} - {} : {}",
                            classWithError.dynamicClassName.toFullClassName(),
                            cause.getClass().getSimpleName(),
                            cause.getMessage());
                });
            }
        }
    }

    private void resolveDependencies(Set<DynamicClassName> dependencies, DependenciesContext dependenciesContext, boolean rootResolve) {
        // resolve dependencies
        Set<DynamicClassName> additionalDependencies = new HashSet<>();

        for (DynamicClassName dependency : dependencies) {
            // split into outer and optional inner class
            DynamicClassName dependencyOuterClass = dependency.getOuterClassName();
            Optional<String> dependencyInnerClass = dependency.getInnerClassName();

            // Xxxx -> Xxxx_Precompiled
            // Xxxx$Yyyy -> Xxxx_Precompiled$Yyyy
            String depPrecompiledClassName = addPrecompiled(dependencyOuterClass.getBaseClassName(),  dependencyOuterClass.getBaseClassName(), null);
            if (dependencyInnerClass.isPresent()) {
                depPrecompiledClassName += "$" + dependencyInnerClass.get();
            }
            String depPrecompiledFullName = dependency.getPackageName() + "." + depPrecompiledClassName;

            // try to get local class
            LocalDynamicClassInfo dependencyLocalClass = localDynamicClassesByClassName.get(dependencyOuterClass);

            DependencyResolution resolution;
            if (isInnerClassOfStaticClass(dependency)) {
                // ##### CASE 5: static inner class of a dynamic class base #####
                // non-dynamic class
                resolution = DependencyResolution.ofNonDynamicClass(dependency);
            } else if (dependencyLocalClass == null) {
                // external Precompiled or static class

                Class<?> precompiledDependencyClass = ReflectionUtils.tryGetClassForName(depPrecompiledFullName);
                if (precompiledDependencyClass != null) {
                    // ##### CASE 1: external Precompiled dynamic class #####
                    // TODO: if class is generated try to read its metadata???
                    // dynamic already compiled class
                    resolution = DependencyResolution.ofDynamicExternalReadyClass(precompiledDependencyClass, DynamicClassName.forClassName(depPrecompiledFullName).toFullClassName());
                } else {
                    // ##### CASE 2: non-dynamic class #####
                    // non-dynamic class
                    resolution = DependencyResolution.ofNonDynamicClass(dependency);
                }
            } else if (dependencyLocalClass.error != null) {
                throw new FhException("Dependency class " + dependencyLocalClass.dynamicClassName.toFullClassName() + " has errors");
            } else if (dependencyLocalClass.needsCompilation) {
                // ##### CASE 3: local dynamic not yet ready (pending) class #####
                resolution = DependencyResolution.ofDynamicPendingClass(dependency.getPackageName(),
                        depPrecompiledClassName, dependencyLocalClass.getMetadata(AREA_HANDLERS));
            } else {
                // ##### CASE 4: local dynamic ready class #####
                // load again - it could change after temporaty during building info
                Class<?> readyClass = ReflectionUtils.getClassForName(depPrecompiledFullName);
                resolution = DependencyResolution.ofDynamicReadyClass(readyClass, dependencyLocalClass.getMetadata(AREA_HANDLERS));
            }
            dependenciesContext.putResulotion(dependency, resolution);

            // rules can depends on types that are related to main dependent type, UC on Form internal model, UC on external UC params
            if (rootResolve && resolution.isDynamicClass() && resolution.getMetadata() != null){
                additionalDependencies.addAll(resolution.getMetadata().getDependencies());
            }
        }

        if (!additionalDependencies.isEmpty()) {
            additionalDependencies.removeAll(dependenciesContext.listDependencies());
            resolveDependencies(additionalDependencies, dependenciesContext, false);
        }
    }

    private void readPrecompiledClassInfo(LocalDynamicClassInfo classInfo) {
        LOGGER.debug("Trying to read precompiled class {}", classInfo.fullTargetClassName);
        try {
            Class<?> precompiledClass = tryGetTemporaryClass(classInfo.fullTargetClassName);
            if (precompiledClass != null) {
                if (!DynamicClassCompiler.isCoreLiteTarget()) {
                    classInfo.precompiledClassXMLTimestamps = DependencyTimestampJavaGenerator.getXmlTimestamps(precompiledClass);
                }
                classInfo.precompiledClass = precompiledClass;
            }
        } catch (Throwable e) {
            LOGGER.error("Error while getting precompiled class info {}", classInfo.fullTargetClassName, e);
            if (failOnError) {
                failOnError("", e);
            } else {
                classInfo.error = e;
            }
        }
    }

    private List<Path> findXmlFiles(Path sourceDirectory, String extension) {
        List<Path> foundFormFiles = new ArrayList<>();
        String fullExtension = "." + extension;
        try {
            Files.walkFileTree(sourceDirectory, new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().endsWith(fullExtension)) {
                        foundFormFiles.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    LOGGER.warn("Cannot check {} - {}: {}", file.toAbsolutePath().toString(), exc.getClass().getSimpleName(), exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Exception while searching for form files in " + sourceDirectory.toString(), e);
        }

        return foundFormFiles;
    }

    private boolean isFormsFile(Path file) throws IOException {
        return file.getFileName().toString().endsWith(".frm");
    }

    private Class<? extends Form> findFormClass(String formClass) {
        try {
            return (Class<? extends Form>) FhCL.classLoader.loadClass(formClass);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static void suppressInternalLogs() {
        configureLogger(LoggerFactory.getLogger("org.springframework"), Level.WARN);
        configureLogger(LoggerFactory.getLogger(FormReader.class), Level.WARN);
        configureLogger(LoggerFactory.getLogger(ReflectionUtils.class), Level.WARN);
        removeNonConsoleAppenders();
    }

    private static void configureLogger(Logger logger, Level level) {
        if (logger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger.class.cast(logger).setLevel(level);
        }
    }

    private static void removeNonConsoleAppenders() {
        Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (logger instanceof ch.qos.logback.classic.Logger) {
            List<Appender<ILoggingEvent>> appenders = new ArrayList<>();
            ch.qos.logback.classic.Logger.class.cast(logger).iteratorForAppenders().forEachRemaining(appenders::add);
            for (Appender<ILoggingEvent> appender : appenders) {
                if (!(appender instanceof ConsoleAppender)) {
                    ch.qos.logback.classic.Logger.class.cast(logger).detachAppender(appender);
                    appender.stop();
                }
            }
        }
    }

    private Class<?> getTemporaryClass(String fullClassName) throws ClassNotFoundException {
        return Class.forName(fullClassName, true, temporaryClassLoader);
    }

    private Class<?> tryGetTemporaryClass(String fullClassNamel) {
        try {
            return getTemporaryClass(fullClassNamel);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private Map<DynamicClassName, Instant> collectDependencyMetadataTimestamps(DynamicClassName className) {
        Map<DynamicClassName, Instant> timestamps = new HashMap<>();
        collectDependencyMetadataTimestampsImpl(className, timestamps);
        return timestamps;
    }

    private void collectDependencyMetadataTimestampsImpl(DynamicClassName className, Map<DynamicClassName, Instant> timestamps) {
        DynamicClassName outerClassName = className.getOuterClassName();
        LocalDynamicClassInfo localDynamicClassInfo = localDynamicClassesByClassName.get(outerClassName);

        if (isInnerClassOfStaticClass(className)) {
            timestamps.put(className, null);
        } else if (localDynamicClassInfo != null) {
            timestamps.put(outerClassName, localDynamicClassInfo.xmlTimestamp);
            for (DynamicClassName dependency : localDynamicClassInfo.getMetadata(AREA_HANDLERS).getDependencies()) {
                DynamicClassName depTimestampClass = isInnerClassOfStaticClass(dependency) ? dependency : dependency.getOuterClassName();
                if (!timestamps.containsKey(depTimestampClass)) {
                    collectDependencyMetadataTimestampsImpl(dependency, timestamps);
                }
            }
        } else {
            Class<?> externalPrecompiled = ReflectionUtils.tryGetClassForName(addPrecompiled(outerClassName.toFullClassName(),  outerClassName.toFullClassName(), null));
            if (externalPrecompiled == null) {
                timestamps.put(outerClassName, null);
            } else {
                // this list contains timestamps of this class and all depedency classes - no need to collect them recursively
                Map<DynamicClassName, Instant> precompiledTimestamps = DependencyTimestampJavaGenerator.getXmlTimestamps(externalPrecompiled);
                // go one by one: check for conflict and add to already gathered timestamps
                for (Map.Entry<DynamicClassName, Instant> dependencyTimestampEntry : precompiledTimestamps.entrySet()) {
                    DynamicClassName dependencyClassName = dependencyTimestampEntry.getKey();
                    Instant newDependencyTimestamp = dependencyTimestampEntry.getValue();
                    if (timestamps.containsKey(dependencyClassName)) {
                        Instant currentDependencyTimestamp = timestamps.get(dependencyClassName);
                        if (!Objects.equals(currentDependencyTimestamp, newDependencyTimestamp)) {
                            throw new FhException(String.format(
                                    "XML timestamp conflict occured. %s in %s has %s timestamp while other source gives %s timestamp.",
                                    dependencyClassName.toFullClassName(), externalPrecompiled.getName(),
                                    toString(newDependencyTimestamp), toString(currentDependencyTimestamp)));
                        }
                    }
                    timestamps.put(dependencyClassName, newDependencyTimestamp);
                }
            }
        }
    }

    private String addPrecompiled(String className, String fullClassName, DynamicClassArea area) {
        if (!DynamicClassCompiler.isCoreLiteTarget()) {
            return className + DynamicClassRepository.PRECOMPILED_CLASS_SUFFIX;
        }
        else if (area == DynamicClassArea.FORM && DynamicClassCompiler.isCoreLiteTarget()) {
            if (ReflectionUtils.tryGetClassForName(fullClassName) != null) {
                return className + FormsUtils.EXTENDED_SUFIX;
            }
        }
        return className;
    }

    private String toString(Instant timestamp) {
        if (timestamp == null) {
            return "<empty>";
        } else {
            return TIMESTAMP_FORMAT.format(timestamp);
        }
    }

    /**
     * Checks if static given class is inner class of a static class.
     */
    private boolean isInnerClassOfStaticClass(DynamicClassName dynamicClassName) {
        return dynamicClassName.getInnerClassName().isPresent() && ReflectionUtils.tryGetClassForName(dynamicClassName.toFullClassName()) != null;
    }
}
