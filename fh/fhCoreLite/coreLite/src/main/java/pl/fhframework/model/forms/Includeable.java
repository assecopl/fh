package pl.fhframework.model.forms;

import java.util.List;

/**
 * Created by szkiladza on 15.02.2017.
 */
public interface Includeable {

    List<Component> getIncludedComponents();

    /**
     * Method is invoked after method processComponent in Form is executed on all components.
     */
    default void afterNestedComponentsProcess() {
    }
}
