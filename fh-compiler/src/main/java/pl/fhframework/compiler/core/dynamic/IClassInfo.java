package pl.fhframework.compiler.core.dynamic;

import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.subsystems.Subsystem;

import java.time.Instant;

/**
 * Interface of dynamic class information
 */
public interface IClassInfo {

    /**
     * Returns XML file's URL (full path, may be in JAR)
     */
    public DynamicClassFileDescriptor getXmlFile();

    /**
     * Returns subsystem
     */
    public Subsystem getSubsystem();

    /**
     * Returns dynamic class area
     */
    public DynamicClassArea getArea();

    /**
     * Returns dynamic class name (package + base name)
     */
    public DynamicClassName getClassName();

    /**
     * Checks if class is dynamic
     */
    public boolean isDynamic();

    default boolean isValid() {
        return true;
    }

    default Instant getLastModification() {
        return Instant.MIN;
    }
}
