package pl.fhframework.core.model;

/**
 * @author Paweł Ruta
 */
public interface BaseEntity<T> {

    T getEntityId();

    void setEntityId(T id);
}
