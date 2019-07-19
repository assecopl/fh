package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLMetadataSubelements;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.CallbackActionBinding;
import pl.fhframework.binding.IActionCallback;
import pl.fhframework.binding.IActionCallbackContext;
import pl.fhframework.events.IFormElementWithKeySupport;
import pl.fhframework.events.OnKeyEvent;
import pl.fhframework.model.dto.InMessageEventData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

/**
 *  Base component for input fields (like CheckBox, InputText etc.) with key events support.
 */
public abstract class BaseInputFieldWithKeySupport extends BaseInputField implements IFormElementWithKeySupport {

    @Getter
    @XMLProperty
    @DocumentedComponentAttribute(value = "Represents use case's action executed each time key(s) defined in 'keyEvent' are being pressed.")
    @DesignerXMLProperty(priority = 120, functionalArea = BEHAVIOR)
    private ActionBinding onKeyEvent;

    @Getter
    @Setter
    @XMLProperty
    @DesignerXMLProperty(priority = 121, functionalArea = BEHAVIOR)
    @DocumentedComponentAttribute(value = "Defines pipe-separated list of key definitions that will call 'onKeyEvent' action. Eg. ENTER or ENTER|CTRL+ALT+A|CTRL+B|SPACE")
    private String keyEvent;

    @Getter
    @Setter
    @XMLMetadataSubelements
    private List<OnKeyEvent> keyEventHandlers = new ArrayList<>();

    public BaseInputFieldWithKeySupport(Form form) {
        super(form);
    }

    @Override
    public void init() {
        super.init();
        initKeyHandlers();
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        Optional<ActionBinding> keyHandler = getKeyEventHandler(eventData);
        if (keyHandler.isPresent()) {
            return keyHandler;
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public void setOnKeyEvent(ActionBinding onKeyEvent) {
        this.onKeyEvent = onKeyEvent;
    }

    public IActionCallbackContext setOnKeyEvent(IActionCallback onKeyEvent) {
        return CallbackActionBinding.createAndSet(onKeyEvent, this::setOnKeyEvent);
    }
}
