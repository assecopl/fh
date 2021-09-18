package pl.fhframework.events;

import pl.fhframework.binding.ActionBinding;
import pl.fhframework.model.dto.InMessageEventData;

import java.util.List;
import java.util.Optional;

/**
 * Form element with keyboard event support.
 */
public interface IFormElementWithKeySupport {

    public static final String ON_KEY_EVENT = "onKeyEvent";
    public static final String ON_KEY_EVENT_HASH = "onKeyEvent#";

    public default Optional<ActionBinding> getKeyEventHandler(InMessageEventData eventData) {
        if (eventData.getEventType().equals(ON_KEY_EVENT)) {
            return Optional.ofNullable(getOnKeyEvent());
        } else if (eventData.getEventType().startsWith(ON_KEY_EVENT_HASH)) {
            String keyEventHandlerId = eventData.getEventType().substring(ON_KEY_EVENT_HASH.length());
            for (OnKeyEvent handler : getKeyEventHandlers()) {
                if (keyEventHandlerId.equals(handler.getId())) {
                    return Optional.ofNullable(handler.getOnKeyEvent());
                }
            }
        }
        return Optional.empty();
    }

    public default void initKeyHandlers() {
        for (OnKeyEvent keyPressedHandler : getKeyEventHandlers()) {
            keyPressedHandler.init();
        }
    }

    public ActionBinding getOnKeyEvent();

    public String getKeyEvent();

    public void setOnKeyEvent(ActionBinding actionBinding);

    public void setKeyEvent(String definition);

    public List<OnKeyEvent> getKeyEventHandlers();

    public void setKeyEventHandlers(List<OnKeyEvent> keyPressedHandlers);
}