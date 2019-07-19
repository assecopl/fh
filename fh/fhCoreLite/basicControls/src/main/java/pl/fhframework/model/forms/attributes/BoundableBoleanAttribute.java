package pl.fhframework.model.forms.attributes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

public abstract class BoundableBoleanAttribute extends Attribute<Boolean> implements BoundableAttribute {

    private Boolean value;

    @JsonIgnore
    @Getter
    @Setter
    @XMLProperty(value = "value")
    private ModelBinding modelBinding;

    public BoundableBoleanAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component);
        if (modelBinding != null) {
            BindingResult bindingResult = modelBinding.getBindingResult();
            String attributeValueString = convertBindingToString(bindingResult);
            this.value = Boolean.TRUE.toString().equalsIgnoreCase(attributeValueString);
        } else {
            this.value = getDefaultValue();
        }
        this.modelBinding = modelBinding;
    }

    @JsonIgnore
    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @JsonIgnore
    @Override
    public abstract String getXmlValue();

    @Override
    public Boolean getDefaultValue() {
        return Boolean.FALSE;
    }

    @Override
    public ElementChanges updateView(Component component, ElementChanges elementChanges) {
        if (this.modelBinding != null) {
            BindingResult bindingResult = this.modelBinding.getBindingResult();
            if (bindingResult != null) {
                boolean newValue = Boolean.valueOf(component.convertBindingValueToString(bindingResult));
                if (newValue != value) {
                    this.value = newValue;
                    elementChanges.addChange(getXmlValue(), this.value);
                }
            }
        }
        return elementChanges;
    }

    @Override
    public void updateModel(Component component, ValueChange valueChange) {
        Boolean pinnedAttributeNewValue = valueChange.getBooleanAttribute(getXmlValue());
        if (pinnedAttributeNewValue != null && pinnedAttributeNewValue.booleanValue() != this.value) {
            this.value = pinnedAttributeNewValue;
            updateBindingForValue(pinnedAttributeNewValue, this.modelBinding, this.value);
        }
    }
}
