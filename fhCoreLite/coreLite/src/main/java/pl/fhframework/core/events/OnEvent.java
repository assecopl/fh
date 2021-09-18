package pl.fhframework.core.events;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

/**
 * Created by krzysztof.kobylarek on 2017-01-11.
 */
@Control
@JsonIgnoreType
public class OnEvent extends Component {

    private static final String NAME_ATTR = "name";
    private static final String HANDLER_ATTR = "handler";

    @Getter
    @Setter
    @XMLProperty(value = NAME_ATTR)
    private String eventName;

    @Getter
    @Setter
    @XMLProperty(value = HANDLER_ATTR)
    private String eventHandler;

    public OnEvent(Form form) {
        super(form);
    }

    @Override
    public void init() {
        if (eventHandler == null) {
            eventHandler = eventName;
        }
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventHandler() {
        return eventHandler;
    }
}
