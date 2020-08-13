package pl.fhframework.model.forms.attributes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Setter;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

import java.util.Objects;

import lombok.Getter;

@DocumentedComponentAttribute(value = "Defines state of floating group", type = Enum.class, defaultValue = "PINNED_MINIMIZED", boundable = true)
@XMLProperty(FloatingGroupStateAttribute.STATE_ATTR)
public class FloatingGroupStateAttribute extends Attribute<FloatingGroupStateAttribute.FloatingState> implements BoundableAttribute {
    public static final String STATE_ATTR = "floatingState";

    private FloatingGroupStateAttribute.FloatingState value;

    @Getter
    @Setter
    @JsonIgnore
    private ModelBinding modelBinding;

    private FloatingOnlyAttribute floatingOnlyAttribute;

    public FloatingGroupStateAttribute(Form form, Component component, ModelBinding<FloatingGroupStateAttribute.FloatingState> modelBinding, FloatingOnlyAttribute floatingOnlyAttribute) {
        super(form, component);
        if (modelBinding != null) {
            BindingResult bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {
                try {
                    if (bindingResult.getValue() instanceof String) {
                        this.value = FloatingGroupStateAttribute.FloatingState.valueOf((String) bindingResult.getValue());
                    } else {
                        this.value = (FloatingGroupStateAttribute.FloatingState) bindingResult.getValue();
                    }
                } catch (IllegalArgumentException e) {
                    FhLogger.warn("Did not find matching value: \"" + bindingResult.getValue() + "\" for floatingState enum.");
                }
            }
        } else {
            value = getDefaultValue();
        }
        this.modelBinding = modelBinding;
        this.floatingOnlyAttribute = floatingOnlyAttribute;
        normalizeValue();
    }

    @Override
    public FloatingGroupStateAttribute.FloatingState getValue() {
        return value;
    }

    @Override
    public void setValue(FloatingGroupStateAttribute.FloatingState value) {
        this.value = value;
    }

    @Override
    public String getXmlValue() {
        return STATE_ATTR;
    }

    @Override
    public FloatingGroupStateAttribute.FloatingState getDefaultValue() {
        return FloatingGroupStateAttribute.FloatingState.PINNED_MINIMIZED;
    }

    @Override
    public ElementChanges updateView(Component component, ElementChanges elementChanges) {
        if (this.modelBinding != null) {
            BindingResult bindingResult = this.modelBinding.getBindingResult();
            if (bindingResult != null) {
                final FloatingState newValue = component.convertValue(bindingResult.getValue(), FloatingState.class);
                if (!Objects.equals(newValue, value)) {
                    this.value = newValue;
                    normalizeValue();
                    elementChanges.addChange(getXmlValue(), this.value);
                }
            }
        }
        return elementChanges;
    }

    private void normalizeValue() {
        // FH-3545
        if (Boolean.TRUE.equals(this.floatingOnlyAttribute.getValue())) {
            if (FloatingState.PINNED_MINIMIZED == value) {
                value = FloatingState.UNPINNED_MINIMIZED;
            } else if (FloatingState.PINNED_MAXIMIZED == value) {
                value = FloatingState.UNPINNED_MAXIMIZED;
            }
        }
    }

    @Override
    public void updateModel(Component component, ValueChange valueChange) {
        final String state = valueChange.getStringAttribute(getXmlValue());
        FloatingState newValue = component.convertValue(state, FloatingState.class);
        if(!Objects.equals(newValue, this.value)){
            updateBindingForValue(newValue, this.modelBinding, this.modelBinding.getBindingExpression());
            this.value = newValue;
        }
    }

    public enum FloatingState {
        PINNED_MINIMIZED(true, false),
        PINNED_MAXIMIZED(true, true),
        UNPINNED_MINIMIZED(false, false),
        UNPINNED_MAXIMIZED(false, true);

        private boolean pinned;
        private boolean fullScreen;

        FloatingState(boolean pinned, boolean fullScreen) {
            this.pinned = pinned;
            this.fullScreen = fullScreen;
        }

        public boolean isPinned() {
            return pinned;
        }

        public void setPinned(boolean pinned) {
            this.pinned = pinned;
        }

        public boolean isFullScreen() {
            return fullScreen;
        }

        public void setFullScreen(boolean fullScreen) {
            this.fullScreen = fullScreen;
        }

        public FloatingState toPinned() {
            if (isFullScreen()) {
                return PINNED_MAXIMIZED;
            } else {
                return PINNED_MINIMIZED;
            }
        }

        public FloatingState toFloating() {
            if (isFullScreen()) {
                return UNPINNED_MAXIMIZED;
            } else {
                return UNPINNED_MINIMIZED;
            }
        }
    }
}
