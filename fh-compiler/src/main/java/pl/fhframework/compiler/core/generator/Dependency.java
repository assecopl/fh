package pl.fhframework.compiler.core.generator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.subsystems.Subsystem;

import java.util.Optional;

@Getter
@Setter
@Builder
public class Dependency {
    private String name;

    private DynamicClassArea type;

    private Subsystem module;

    // available if provided == false
    private Object metadata;

    // available if provided == true
    @JsonIgnore
    private Class<?> staticClass;

    // true value means the dependency is not generable and is provided (e.g. Java class on server side)
    private boolean provided = false;

    // can be true only if provided == true, means that it's provided by fh core
    private boolean core = false;

    <T> Optional<T> getMetadata() {
        return (Optional<T>) Optional.ofNullable(metadata);
    }
}
