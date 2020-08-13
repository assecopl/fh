package pl.fhframework.model.forms.attributes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Setter;
import pl.fhframework.BindingResult;
import pl.fhframework.XmlAttributeReader;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

import java.util.Objects;

import lombok.Getter;

public abstract class AbstractBoundableIntegerAttribute extends AbstractIntegerAttribute implements BoundableAttribute{

    @Getter
    @Setter
    @JsonIgnore
    private ModelBinding modelBinding;

    public AbstractBoundableIntegerAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
        this.modelBinding = modelBinding;
    }

    @Override
    public ElementChanges updateView(Component component, ElementChanges elementChanges) {
        if (this.modelBinding != null) {
            BindingResult bindingResult = this.modelBinding.getBindingResult();
            if (bindingResult != null) {
                Integer newValue = component.convertValue(bindingResult.getValue(), Integer.class);
                if (!Objects.equals(newValue, value)) {
                    this.value = newValue;
                    elementChanges.addChange(getXmlValue(), this.value);
                }
            }
        }
        return elementChanges;
    }

    @Override
    public void updateModel(Component component, ValueChange valueChange) {
        final String newValue = valueChange.getStringAttribute(getXmlValue());
        if (newValue != null && !Objects.equals(component.convertValue(newValue, Integer.class), this.value)) {
            updateBindingForValue(newValue, this.modelBinding, this.value);
        }
    }
}
