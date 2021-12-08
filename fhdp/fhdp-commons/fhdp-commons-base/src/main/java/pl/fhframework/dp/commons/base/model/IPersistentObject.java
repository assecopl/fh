package pl.fhframework.dp.commons.base.model;

import java.io.Serializable;

public interface IPersistentObject<T> extends Serializable {

    public T getId();

    public void setId(T id);
}
