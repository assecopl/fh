package pl.fhframework.compiler.core.dynamic;

import pl.fhframework.core.dynamic.DynamicClassName;

/**
 * Created by pawel.ruta on 2018-02-20.
 */
public interface IDynamicClassResolver {
    <M extends DynamicClassMetadata> M getMetadata(DynamicClassName dynamicClassName);

    boolean isRegisteredDynamicClass(DynamicClassName dynamicClassName);

    boolean isRegisteredStaticClass(DynamicClassName dynamicClassName);

    default boolean isRegistered(DynamicClassName dynamicClassName) {
        return isRegisteredDynamicClass(dynamicClassName) || isRegisteredStaticClass(dynamicClassName);
    }

    Class<?> getOrCompileDynamicClass(DynamicClassName formClassName);
}
