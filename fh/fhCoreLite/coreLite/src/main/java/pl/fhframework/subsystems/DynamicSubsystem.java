package pl.fhframework.subsystems;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.XmlAttributeReader;
import pl.fhframework.configuration.FHConfiguration;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.util.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

class DynamicSubsystem extends Subsystem {

    private static final long MINIMAL_TIME_BETWEEN_SOURCE_REFRESH = 1000;
    private static final String SRC_MAIN_JAVA = Paths.get("src/main/java").toString();

    /**
     * Base package used as a root where use case classes are defined (derived from basePackage attribute of subsystem element tag from .sys file)
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private String basePackage;

    /**
     * In case of dynamic subsystem (one defined by .sys file) resolves to .sys file URL
     */
    @Getter
    @Setter(AccessLevel.PACKAGE)
    @JsonIgnore
    private FhResource source;

    @Getter
    private long sourceVersion;

    DynamicSubsystem(XmlAttributeReader xmlAttributeReader, FhResource loadedFileSysUrl) {
        super(xmlAttributeReader.getAttributeValue("name"), xmlAttributeReader.getAttributeValue("label"),
                xmlAttributeReader.getAttributeValue("productLabel"), xmlAttributeReader.getAttributeValue("productUUID"),
                xmlAttributeReader.getAttributeValue("access"), getBasePath(loadedFileSysUrl, xmlAttributeReader.getAttributeValue("name")),
                detectOverridenResourcesPath(getBasePath(loadedFileSysUrl, xmlAttributeReader.getAttributeValue("name"))),
                getBaseClassPath(loadedFileSysUrl), xmlAttributeReader.getAttributeValue("dependsOn"));
        this.source = loadedFileSysUrl;
        this.basePackage = xmlAttributeReader.getAttributeValue("basePackage");
        this.sourceVersion = FileUtils.getLastModified(loadedFileSysUrl).toEpochMilli();
    }

    /**
     * Gets base path for module files. By default it is based on module.sys location in class path.
     * If module.sys is found in target/classes and src/main/java is present, then src/main/java is used as base path.
     *
     * Any deduction may be overiden by using:
     * -Dfhframework.path.MODULE_NAME=MODULE_DIR or -Dfhframework.basePath=DIR_WITH_MODULES_DIRS
     */
    private static FhResource getBasePath(FhResource loadedFileSysUrl, String subsystemName) {
        // URL deduced path will not be empty only if URL is a plain file
        Optional<FhResource> urlDeducedPath = Optional.of(loadedFileSysUrl);

        // get overriden path, if URL deduced path is not preset this path must by preset otherwise this will cause exception
        //Optional<URL> overridenPath = FHConfiguration.getOverridenSubsystemPath(subsystemName, urlDeducedPath.isPresent());
        Optional<FhResource> overridenPath = FHConfiguration.getOverridenSubsystemPath(subsystemName, urlDeducedPath.isPresent());

        // at this point overridenPath or urlDeducedPath is present
        FhResource basePath;
        String pathDetectionMethod;
        if (overridenPath.isPresent()) {
            pathDetectionMethod = "-D param";
            basePath = overridenPath.get();
        } else {
            basePath = urlDeducedPath.get().toExternalResource();
            basePath = FhResource.get(
                    basePath.getURI().getPath().endsWith("/")
                            ? basePath.getURI().resolve("..")
                            : basePath.getURI().resolve("."));  // drop ModuleRegistry.MODULE_CONFIGURATION_FILE_PATH (modules.sys) from file path

            pathDetectionMethod = "module.sys based";
            if (basePath.toExternalPath().matches(".*[\\\\/]target[\\\\/]classes[\\\\/]?")) {
                FhResource src =  FileUtils.getParent(FileUtils.getParent(basePath)).resolve("src/main/java");
                if (Files.isDirectory(src.getExternalPath())) {
                    basePath = src;
                    pathDetectionMethod = "sources detected";
                }
            }
        }
        FhLogger.info(DynamicSubsystem.class, "Base path for module {} is {} ({})", subsystemName, basePath.getURL().toString(), pathDetectionMethod);

        return basePath;
    }

    /**
     * Detects if base module path ends with src/main/java. If so, returns overriden resources path as src/main/resources.
     */
    private static FhResource detectOverridenResourcesPath(FhResource basePath) {
        if (basePath.toExternalPath().matches(".*[\\\\/]src[\\\\/]main[\\\\/]java[\\\\/]?")) {
            return basePath.resolve("../resources/" + RESOURCES_DIRECTORY);
        } else {
            return null; // default
        }
    }

    private static FhResource getBaseClassPath(FhResource loadedFileSysUrl) {
        return FileUtils.getParent(loadedFileSysUrl);
    }

    private static String getPackagePath(String loadedDPUFileName) {
        return loadedDPUFileName.substring(0, loadedDPUFileName.lastIndexOf(File.separatorChar));
    }

    @Override
    public boolean requiresUpdate() {
        long delta = System.currentTimeMillis() - sourceVersion;
        if (delta < MINIMAL_TIME_BETWEEN_SOURCE_REFRESH) {
            return false;
        } else {
            long lastVersion = FileUtils.getLastModified(source).toEpochMilli();
            return lastVersion != sourceVersion;
        }
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    public static class CachedClass {
        Class<? extends IUseCase> clazz;
        long compilationTimePoint;
        private long lastSourceUpdateVerificationTimePoint;
        private File sourceFile;
        private long lastSourceModificationTimePoint;

        CachedClass(Class<? extends IUseCase> clazz, long compilationTimePoint, File sourceFile) {
            this.clazz = clazz;
            this.compilationTimePoint = compilationTimePoint;
            this.lastSourceUpdateVerificationTimePoint = System.currentTimeMillis();
            this.lastSourceModificationTimePoint = sourceFile.lastModified();
            this.sourceFile = sourceFile;
        }

        boolean isCurrent() {
            if (System.currentTimeMillis() - this.lastSourceUpdateVerificationTimePoint > MINIMAL_TIME_BETWEEN_SOURCE_REFRESH) {
                lastSourceModificationTimePoint = sourceFile.lastModified();
                lastSourceUpdateVerificationTimePoint = System.currentTimeMillis();
            }
            return lastSourceModificationTimePoint < compilationTimePoint;
        }
    }
}
