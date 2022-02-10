package pl.fhframework.compiler.core.dynamic.utils;

import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DynamicClassResolver;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;

/**
 * Use case model utility methods.
 */
public class ModelUtils extends UseCaseModelUtils {
    public ModelUtils(DependenciesContext dependenciesContext) {
        this.dynamicClassRepository = new DynamicClassResolver(dependenciesContext);
    }
}
