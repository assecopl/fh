package pl.fhframework.core.forms.iterators;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * Interface of a component that directly contains an indexable bindings
 */
public interface IIndexedBindingOwner {

    /**
     * Name of parameter with array of all iterator indices.
     */
    public static final String INDICES_ARRAY_PARMETER_NAME = "__indicesArray__";

    @JsonIgnore
    public List<IRepeatableIteratorInfo> getIteratorInfos();
}
