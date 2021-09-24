package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.KeyboardEventShortcutEnum;
import pl.fhframework.model.dto.InMessageEventData;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.CONTENT;

@Control(parents = {Form.class}, canBeDesigned = true)
@DocumentedComponent(documentationExample = true, value = "Keyboard shortcut trigger for a form event", icon = "fa fa-keyboard")
@DesignerControl(defaultWidth = 1)
public class KeyboardEvent extends NonVisualFormElement {
    private static final String ATTR_SHORTCUT = "shortcut";
    private static final String ATTR_EVENT = "event";
    private static final String KEYBOARD_EVENT_ICON = "fa fa-keyboard";

    @Getter
    @XMLProperty(required = true, value = ATTR_EVENT)
    @DesignerXMLProperty(commonUse = true, functionalArea = CONTENT, priority = 90)
    @DocumentedComponentAttribute(value = "Event to be triggered")
    private ActionBinding eventBinding;

    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_SHORTCUT)
    @DesignerXMLProperty(functionalArea = CONTENT, commonUse = true)
    @DocumentedComponentAttribute(value = "Keyboard shortcut")
    private KeyboardEventShortcutEnum shortcut;

    public KeyboardEvent(Form form) {
        super(form);
    }

    @Override
    public String getNonVisualToolboxIcon() {
        return KEYBOARD_EVENT_ICON;
    }

    public void setEventBinding(ActionBinding eventBinding) {
        this.eventBinding = eventBinding;
    }

    public IActionCallbackContext setEventBinding(IActionCallback onTimer) {
        return CallbackActionBinding.createAndSet(onTimer, this::setEventBinding);
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        return Optional.ofNullable(eventBinding);
    }
}