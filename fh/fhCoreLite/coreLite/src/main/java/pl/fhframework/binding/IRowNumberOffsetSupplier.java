package pl.fhframework.binding;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by Piotr on 2017-01-26.
 */
public interface IRowNumberOffsetSupplier {

    @JsonIgnore
    public int getRowNumberOffset();
}
