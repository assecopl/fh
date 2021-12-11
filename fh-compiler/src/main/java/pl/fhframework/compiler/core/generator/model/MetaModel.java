package pl.fhframework.compiler.core.generator.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class MetaModel implements Wrapper {
    List<String> dependencies;

    public abstract String getId();
}
