package pl.fhframework.model.forms;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.*;
import pl.fhframework.binding.*;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.InMessageEventData;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.*;

/**
 * Class representing xml component of Timer.
 */
@Control(parents = {Form.class}, canBeDesigned = true)
@DocumentedComponent(value = "Timer which periodically runs events", icon = "fa fa-clock")
@DesignerControl(defaultWidth = 1)
public class Timer extends NonVisualFormElement {

    public static final String ATTR_INTERVAL = "interval";
    public static final String ATTR_ACTIVE = "active";
    public static final String ATTR_ON_TIMER = "onTimer";
    private static final String TIMER_ICON = "fa fa-clock";

    @Getter
    @XMLProperty
    @DesignerXMLProperty(commonUse = true, functionalArea = BEHAVIOR, priority = 90)
    @DocumentedComponentAttribute(value = "If the timer runs that method will be executed.")
    private ActionBinding onTimer;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_INTERVAL)
    @DesignerXMLProperty(commonUse = true, functionalArea = CONTENT, priority = 100)
    @DocumentedComponentAttribute(boundable = true, value = "Time in seconds between subsequent times runs.")
    private ModelBinding<Integer> intervalBinding;

    @Getter
    private Integer interval;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(required = true, value = ATTR_ACTIVE, defaultValue = "true")
    @DesignerXMLProperty(commonUse = true, functionalArea = CONTENT, priority = 101)
    @DocumentedComponentAttribute(boundable = true, value = "Flag which defines if the timer is active.", defaultValue = "true")
    private ModelBinding<Boolean> activeBinding;

    @Getter
    private boolean active;

    public Timer(Form form) {
        super(form);
    }

    @Override
    public String getNonVisualToolboxIcon() {
        return TIMER_ICON;
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (eventData.getEventType().equals(ATTR_ON_TIMER)) {
            return Optional.ofNullable(onTimer);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    @Override
    public ElementChanges updateView() {
        ElementChanges elementChanges = super.updateView();
        if (activeBinding != null) {
            active = activeBinding.resolveValueAndAddChanges(this, elementChanges, active, ATTR_ACTIVE);
        }
        if (intervalBinding != null) {
            interval = intervalBinding.resolveValueAndAddChanges(this, elementChanges, interval, ATTR_INTERVAL);
        }
        if (elementChanges.containsAnyChanges()) {
            refreshView();
        }
        return elementChanges;
    }

    public void setOnTimer(ActionBinding onTimer) {
        this.onTimer = onTimer;
    }

    public IActionCallbackContext setOnTimer(IActionCallback onTimer) {
        return CallbackActionBinding.createAndSet(onTimer, this::setOnTimer);
    }
}