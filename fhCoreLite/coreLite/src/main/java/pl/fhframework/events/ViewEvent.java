package pl.fhframework.events;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.FormElement;

/**
 * Created by krzysztof.kobylarek on 2016-10-20.
 */
//consider if we would be able to merge it with class EventData
@Getter @Setter
public class ViewEvent<T> {
    Component sourceObject;
    Form<T> sourceForm;
    Object optionalValue;

    public ViewEvent(Component sourceObject, Form<T> sourceForm) {
        this.sourceObject = sourceObject;
        this.sourceForm = sourceForm;
    }

    public ViewEvent(Component sourceObject, Form<T> sourceForm, Object optionalValue) {
        this.sourceObject = sourceObject;
        this.sourceForm = sourceForm;
        this.optionalValue = optionalValue;
    }
}
