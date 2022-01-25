package pl.fhframework.compiler.core.dynamic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.subsystems.Subsystem;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

/**
 * Dynamic class XML file descriptor
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DynamicClassFileDescriptor {

    /**
     * URL (full path, may be in JAR)
     */
    private FhResource resource;

    /**
     * Path relative to subsystem base path
     */
    private String relativePath;

    /**
     * Subsystem
     */
    private Subsystem subsystem;

    public static DynamicClassFileDescriptor forURL(URL url, String relativePath, Subsystem subsystem) {
        return new DynamicClassFileDescriptor(FhResource.get(url), relativePath, subsystem);
    }

    public static DynamicClassFileDescriptor forURI(URI uri, String relativePath, Subsystem subsystem) {
        return new DynamicClassFileDescriptor(FhResource.get(uri), relativePath, subsystem);
    }

    public static DynamicClassFileDescriptor forPath(Path path, String relativePath, Subsystem subsystem) {
        return new DynamicClassFileDescriptor(FhResource.get(path) , relativePath, subsystem);
    }

    public static DynamicClassFileDescriptor forResource(Resource resource, String relativePath, Subsystem subsystem) {
        return new DynamicClassFileDescriptor(FhResource.get(resource) , relativePath, subsystem);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DynamicClassFileDescriptor that = (DynamicClassFileDescriptor) o;

        if (resource != null ? !resource.equals(that.resource) : that.resource != null) return false;
        if (relativePath != null ? !relativePath.equals(that.relativePath) : that.relativePath != null) return false;
        String thisSubsysName = (subsystem != null ? subsystem.getName() : null);
        String thatSubsysName = (that.subsystem != null ? that.subsystem.getName() : null);
        if (thisSubsysName != null ? !thisSubsysName.equals(thatSubsysName) : thatSubsysName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return relativePath != null ? relativePath.hashCode() : 0;
    }
}