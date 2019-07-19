package pl.fhframework.events;

import pl.fhframework.core.generator.ModelElement;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.model.dto.InMessageEventData;

/**
 * Created by Adam Zareba on 03.03.2017. Interface needed for Designer to execute designer actions
 */
@ModelElement(type = ModelElementType.HIDDEN)
public interface IDesignEventSource {

    String ON_DROP_COMPONENT = "onDropComponent";

    String ON_RESIZE_COMPONENT = "onResizeComponent";

    public static boolean isDesignActionAllowed(String actionName) {
        return ON_DROP_COMPONENT.equals(actionName) || ON_RESIZE_COMPONENT.equals(actionName);
    }


    /**
     * Sets default parameter for access to delete functionality in Designer
     * @return Boolean
     */
    default Boolean isDesignDeletable() {
        return true;
    }
}
