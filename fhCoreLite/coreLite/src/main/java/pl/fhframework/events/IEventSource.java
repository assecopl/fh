package pl.fhframework.events;

import pl.fhframework.binding.ActionBinding;
import pl.fhframework.model.dto.InMessageEventData;
import pl.fhframework.model.forms.Form;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Interface need to be implemented by components(like InputText) for executing methods, that are
 * provided by Forms.
 */
public interface IEventSource {

    Optional<ActionBinding> getEventHandler(InMessageEventData eventData);

    /**
     * Checks if event of given type is modification of component. If true this event will not be valid for availablity other than EDIT.
     * @param eventType event type
     * @return if event of given type is modification of component
     */
    default boolean isModificationEvent(String eventType) {
        return true;
    }

    ViewEvent<?> prepareEventDataArgument(InMessageEventData eventData);

    <T> Form<T> getForm();

    <T> Form<T> getEventProcessingForm();
}
