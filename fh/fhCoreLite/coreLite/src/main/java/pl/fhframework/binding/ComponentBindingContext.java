package pl.fhframework.binding;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Piotr on 2017-01-26.
 */
@Getter
public class ComponentBindingContext implements Cloneable {

    private List<RowNumberBindingContext> rowNumberBindingContexts = new ArrayList<>();
    private Map<String, Object> iteratorContext = new HashMap<>();

    @Override
    public ComponentBindingContext clone() throws CloneNotSupportedException {
        ComponentBindingContext clone = new ComponentBindingContext();
        clone.rowNumberBindingContexts.addAll(this.rowNumberBindingContexts);
        return clone;
    }
}
