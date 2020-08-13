package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Gabriel.Kurzac on 2016-11-02.
 */
public interface IGroupingComponent <T extends Component> {
    default void doActionForEverySubcomponent(Consumer<T> action){
        doActionForEverySubcomponent(this::getSubcomponents, action);
        doActionForEverySubcomponent(() -> (List<T>) this.getNonVisualSubcomponents(), action);
    }

    default void doActionForEveryActiveSubcomponent(Consumer<T> action){
        doActionForEverySubcomponent(action);
    }

    default void doActionForEverySubcomponent(Supplier<List<T>> list, Consumer<T> action){
        list.get().stream().forEachOrdered( (T t) -> {
            action.accept(t);
            if (t instanceof IGroupingComponent){
                ((IGroupingComponent)t).doActionForEverySubcomponent(action);
            }
        });
    }

    default void doActionForEverySubcomponentInlcudingRepeated(Consumer<T> action){
        getSubcomponents().stream().forEachOrdered( (T t) -> {
            action.accept(t);
            if (t instanceof IGroupingComponent) {
                ((IGroupingComponent)t).doActionForEverySubcomponentInlcudingRepeated(action);
            }
            if (t instanceof IRepeatableComponentsHolder) {
                ((IRepeatableComponentsHolder)t).doActionForEveryRepeatedSubcomponent((Consumer<Component>) action);
            }
        });
    }

    void addSubcomponent(T formElement);

    void removeSubcomponent(T removedFormElement);

    default public IGroupingComponent getGroupingComponent(Component formElement){
        List<T> subcomponents = getSubcomponents();
        if (subcomponents.contains(formElement) || getNonVisualSubcomponents().contains(formElement)) {
            return this;
        } else {
            for (T subcomp : subcomponents) {
                if (subcomp instanceof IGroupingComponent) {
                    IGroupingComponent groupingComponent = ((IGroupingComponent)subcomp).getGroupingComponent(formElement);
                    if (groupingComponent != null){
                        return groupingComponent;
                    }
                }
            }
            return null;
        }
    }

    //@JsonView(IWithSubelements.class)
    @JsonProperty(value = "subelements")
    List<T> getSubcomponents();

    @JsonProperty(value = "nonVisualSubcomponents")
    List<NonVisualFormElement> getNonVisualSubcomponents();

    default void processComponents() {
    }
}
