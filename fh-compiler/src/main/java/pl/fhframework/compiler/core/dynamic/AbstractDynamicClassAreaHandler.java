package pl.fhframework.compiler.core.dynamic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.subsystems.Subsystem;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Abstract dynamic class area handler
 */
@AllArgsConstructor
public abstract class AbstractDynamicClassAreaHandler<M extends DynamicClassMetadata> {

    /**
     * Filename extension of supported files
     */
    @Getter
    private String xmlFilenameExtension;

    /**
     * Supported dynamic class area.
     */
    @Getter
    private DynamicClassArea supportedArea;

    @Getter
    private boolean searchInBasePackageOnly;

    /**
     * Lists all known area static classes (including classes extended by dynamic classes)
     * @param subsystem subsystem name
     * @return all known area static classes
     */
    public abstract List<Class<?>> listAreaStaticClasses(Subsystem subsystem);

    /**
     * Reads metadata from XML file.
     * @param file file descriptor
     * @return metadata
     */
    public abstract M readMetadata(DynamicClassFileDescriptor file);

    /**
     * Generates class
     * @param metadata metadata
     * @param newClassPackage new class package
     * @param newClassName new class name
     * @param xmlTimestampMethod timestamps method to be included in the generated class
     * @param dependenciesContext context of dependencies
     * @return genereated class content
     */
    public abstract String generateClass(M metadata, String newClassPackage, String newClassName,
                                         GenerationContext xmlTimestampMethod, DependenciesContext dependenciesContext);

    /**
     * Does any area specific tasks after class was compiled
     * @param metadata metadata
     * @param workingDirectory working directory path
     * @param javaFilePath java file path
     * @param newClassPackage new class package
     * @param newClassName new class name
     */
    public void postCompile(M metadata, Path workingDirectory, Path javaFilePath, String newClassPackage, String newClassName) {
    }

    /**
     * Does any area specific tasks after class was loaded
     * @param xmlFile
     * @param clazz loaded class
     * @param metadata metadata
     */
    public void postLoad(DynamicClassFileDescriptor xmlFile, Class<?> clazz, DynamicClassMetadata metadata) {
    }

    /**
     * Does any area specific tasks after all classes from subsystem were loaded
     */
    public void postLoad() {
    }

    /**
     * Does any area specific tasks after all classes from all subsystem were loaded
     */
    public void postAllLoad(IDynamicClassResolver dynamicClassResolver) {
    }

    /**
     * Does any area specific tasks after a class is registered in the repository
     */
    public void postRegisterDynamicClass(M metadata) {
    }

    /**
     * Does any area specific tasks after a class is unregistered in the repository
     */
    public void postUnregisterDynamicClass(M metadata) {
    }

    public void preUpdateDynamicClass(M metadata) {

    }

    public void postUpdateDynamicClass(M metadata) {

    }

    public Instant getLastKnownTimestamp(DynamicClassFileDescriptor xmlFile, DynamicClassMetadata metadata) {
        return FileUtils.getLastModified(xmlFile.getResource());
    }

    public Optional<Class<?>> getReadyClass(DynamicClassMetadata metadata) {
        return Optional.empty();
    }
}
