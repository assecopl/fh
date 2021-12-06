package pl.fhframework.compiler.core.dynamic;

import lombok.Data;
import pl.fhframework.core.dynamic.DynamicClassName;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Dynamic class metadata
 */
@Data
public class DynamicClassMetadata {

    private DynamicClassName dynamicClassName;

    private String displayName;

    private Set<DynamicClassName> dependencies = new LinkedHashSet<>();

    private List<RuntimeErrorDescription> runtimeErrors = new ArrayList<>();
}
