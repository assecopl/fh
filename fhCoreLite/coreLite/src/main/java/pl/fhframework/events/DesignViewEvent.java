package pl.fhframework.events;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

import java.util.List;
import java.util.Map;

/**
 * Created by Adam Zareba on 01.03.2017. Event for form designer purpose. Allows sending additional
 * data over {@link #changedFields} collection.
 */
@Getter
@Setter
public class DesignViewEvent<T> extends ViewEvent<T> {

    private Map<String, Object> changedAttributes;

    public DesignViewEvent(Component sourceObject, Form<T> sourceForm, Map<String, Object> changedAttributes) {
        super(sourceObject, sourceForm);
        this.changedAttributes = changedAttributes;
    }
}
