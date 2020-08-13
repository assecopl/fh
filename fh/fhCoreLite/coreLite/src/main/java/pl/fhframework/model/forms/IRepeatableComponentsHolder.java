package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.function.Consumer;

/**
 * Repeated components holder interface.
 */
public interface IRepeatableComponentsHolder {

    @JsonIgnore
    List<Component> getRepeatedComponents();


    default void doActionForEveryRepeatedSubcomponent(Consumer<Component> action) {
        getRepeatedComponents().stream().forEachOrdered( (Component t) -> {
            action.accept(t);
            if (t instanceof IGroupingComponent) {
                ((IGroupingComponent)t).doActionForEverySubcomponentInlcudingRepeated(action);
            }
            if (t instanceof IRepeatableComponentsHolder) {
                ((IRepeatableComponentsHolder)t).doActionForEveryRepeatedSubcomponent(action);
            }
        });
    }
}
