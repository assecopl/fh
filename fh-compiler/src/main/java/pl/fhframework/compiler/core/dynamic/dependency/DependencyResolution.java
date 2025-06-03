package pl.fhframework.compiler.core.dynamic.dependency;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;

/**
 * Dependency resolution
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DependencyResolution {

    private Class<?> readyClass;

    private String fullClassName;

    private DynamicClassMetadata metadata;

    private boolean isDynamic;

    public static DependencyResolution ofNonDynamicClass(DynamicClassName className) {
        return new DependencyResolution(null, className.toFullClassName(), null, false);
    }

    public static DependencyResolution ofDynamicReadyClass(Class<?> dynamicClass, DynamicClassMetadata metadata) {
        return new DependencyResolution(dynamicClass, dynamicClass.getName(), metadata, true);
    }

    public static DependencyResolution ofDynamicExternalReadyClass(Class<?> dynamicClass, String dynamicClassName) {
        return new DependencyResolution(dynamicClass, dynamicClassName, null, false);
    }

    public static DependencyResolution ofDynamicPendingClass(String newClassPackage, String newClassName, DynamicClassMetadata metadata) {
        return new DependencyResolution(null, newClassPackage + "." + newClassName, metadata, true);
    }

    /**
     * Returns a ready class. It will not be available for pending dynamic classes in cyclic dependencies.
     * @return ready class
     * @throws FhException when the class is not ready yet
     */
    public Class<?> getReadyClass() {
        // lazy static class resolution
        if (!isDynamic && readyClass == null) {
            try {
                readyClass = FhCL.classLoader.loadClass(fullClassName);
            } catch (ClassNotFoundException e) {
                throw new FhException("Declared non-dynamic dependency class " + fullClassName + " not found");
            }
        }

        // at this point ready class should be present for non-pending classes
        if (readyClass != null) {
            return readyClass;
        } else {
            throw new FhException(fullClassName + " dynamic class is not ready yet - you may only use its full name");
        }
    }

    /**
     * Returns a full class name. It is always available.
     * @return full class name
     */
    public String getFullClassName() {
        return fullClassName;
    }

    /**
     * Returns dynamic class metadata. Is available only for dynamic classes.
     * @return dynamic class metadata
     * @throws FhException when the class is not dynamic
     */
    public DynamicClassMetadata getMetadata() {
        if (!isDynamicClass()) {
            throw new FhException(fullClassName + " is not a dynamic class dependency - no dynamic class metadata available");
        }
        return metadata;
    }

    /**
     * Checks if it is a resolution to a dynamic class.
     * @return if it is a resolution to a dynamic class
     */
    public boolean isDynamicClass() {
        return isDynamic;
    }

    /**
     * Checks if it is a resolution to a ready class. May return false for pending (not yet ready) dynamic classes.
     * @return if it is a resolution to a ready class
     */
    public boolean isClassReady() {
        return !isDynamic || readyClass != null;
    }
}
