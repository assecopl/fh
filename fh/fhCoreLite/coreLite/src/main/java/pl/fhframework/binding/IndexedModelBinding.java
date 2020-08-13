package pl.fhframework.binding;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.Getter;
import pl.fhframework.annotations.RepeaterTraversable;

/**
 * An indexed model binding. This is a simple read-only binding. Value is based on indexes of iterators.
 */
@JsonIgnoreType
@RepeaterTraversable
@Getter
public abstract class IndexedModelBinding<T> {

    private String bindingExpression;

    public IndexedModelBinding(String bindingExpression) {
        this.bindingExpression = bindingExpression;
    }

    public abstract T getValue(int[] indices);
}
