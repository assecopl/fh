package pl.fhframework.model.forms;

import java.util.List;

/**
 * Created by szkiladza on 15.02.2017.
 */
public interface Includeable {

    List<Component> getIncludedComponents();

    /**
     * Method is invoked before method processComponent in Form is executed on all components.
     */
    default void activateBindings() {
    }

    /**
     * Method is invoked after method processComponent in Form is executed on all components.
     */
    default void deactivateBindings() {
    }

    /**
     * Method is invoked after method processComponent in Form is executed on all components.
     */
    default void afterNestedComponentsProcess() {
    }

    String getStaticRef();
}
