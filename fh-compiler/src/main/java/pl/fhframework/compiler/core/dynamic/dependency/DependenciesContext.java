package pl.fhframework.compiler.core.dynamic.dependency;

import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dependencies context
 */
public class DependenciesContext {

    private Map<DynamicClassName, DependencyResolution> resolutions = new LinkedHashMap<>();

    /**
     * Put dependency resolution
     * @param className class name
     * @param resolution dependency resolution
     */
    public void putResulotion(DynamicClassName className, DependencyResolution resolution) {
        resolutions.put(className, resolution);
    }

    /**
     * Resolve dependency
     * @param className class name
     * @return dependency resolution
     */
    public DependencyResolution resolve(DynamicClassName className) {
        if (resolutions.containsKey(className)) {
            return resolutions.get(className);
        } else {
            throw new FhException("Not a dependency: " + className + ". Known dependencies are: " + resolutions.keySet());
        }
    }

    /**
     * Is dependency existent
     * @param className class name
     * @return if dependency exists
     */
    public boolean contains(DynamicClassName className) {
        return resolutions.containsKey(className);
    }

    public List<DynamicClassName> listDependencies() {
        return new ArrayList<>(resolutions.keySet());
    }
}
