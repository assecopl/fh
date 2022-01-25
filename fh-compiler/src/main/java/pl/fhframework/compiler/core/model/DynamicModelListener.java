package pl.fhframework.compiler.core.model;

/**
 * Created by pawel.ruta on 2017-05-11.
 */
public interface DynamicModelListener {
    void newClassLoaded(Class clazz, DynamicModelMetadata metadata);
}
