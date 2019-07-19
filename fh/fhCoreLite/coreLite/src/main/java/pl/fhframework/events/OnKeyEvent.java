package pl.fhframework.events;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.CallbackActionBinding;
import pl.fhframework.binding.IActionCallback;
import pl.fhframework.binding.IActionCallbackContext;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

/**
 * Additional key event declaration
 */
@Control
public class OnKeyEvent extends Component {

    @Getter
    @XMLProperty(required = true)
    @DocumentedComponentAttribute(value = "Represents use case's action executed each time key(s) defined in 'keyEvent' are being pressed.")
    @DesignerXMLProperty(priority = 120, functionalArea = BEHAVIOR)
    private ActionBinding onKeyEvent;

    @Getter
    @Setter
    @XMLProperty(required = true)
    @DesignerXMLProperty(priority = 121, functionalArea = BEHAVIOR)
    @DocumentedComponentAttribute(value = "Defines pipe-separated list of key definitions that will call 'onKeyEvent' action. Eg. ENTER or ENTER|CTRL+ALT+A|CTRL+B|SPACE")
    private String keyEvent;

    public OnKeyEvent(Form form) {
        super(form);
    }

    public void setOnKeyEvent(ActionBinding onKeyEvent) {
        this.onKeyEvent = onKeyEvent;
    }

    public IActionCallbackContext setOnKeyEvent(IActionCallback onKeyEvent) {
        return CallbackActionBinding.createAndSet(onKeyEvent, this::setOnKeyEvent);
    }
}
