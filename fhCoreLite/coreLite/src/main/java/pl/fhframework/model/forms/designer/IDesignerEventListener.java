package pl.fhframework.model.forms.designer;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.forms.IGroupingComponent;
import pl.fhframework.model.forms.SpacerService;

/**
 * Interface of component that listens to designer events targeting this component.
 */
public interface IDesignerEventListener {

    /**
     * Executed after adding component to parent, but before doing init on this component.
     * @param parent parent component
     * @param spacerService SpacerService implementation for optional usage
     */
    public default void onDesignerBeforeAdding(IGroupingComponent<?> parent, SpacerService spacerService) {
    }

    /**
     * Adds subcomponent of default type.
     * @param spacerService SpacerService implementation for optional usage
     */
    public default void onDesignerAddDefaultSubcomponent(SpacerService spacerService) {
        FhLogger.error("Not supported yet");
    }
}
