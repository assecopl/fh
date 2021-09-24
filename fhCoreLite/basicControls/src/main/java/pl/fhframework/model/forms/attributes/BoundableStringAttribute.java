package pl.fhframework.model.forms.attributes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Setter;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import pl.fhframework.BindingResult;
import pl.fhframework.XmlAttributeReader;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

import java.util.Objects;

import lombok.Getter;

/**
 * Created by k.czajkowski on 16.02.2017.
 */
public abstract class BoundableStringAttribute extends AbstractStringAttribute implements BoundableAttribute{

    private String value;

    @Getter
    @Setter
    @JsonIgnore
    private ModelBinding modelBinding;

    public BoundableStringAttribute(Form form, Component component, ModelBinding<String> modelBinding) {
        super(form, component, modelBinding);
        if (modelBinding != null) {
            this.value = convertBindingToString(modelBinding.getBindingResult());
        } else {
            this.value = getDefaultValue();
        }
    }

    @JsonIgnore
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
    @Override
    public abstract String getXmlValue();

    @Override
    public String getDefaultValue() {
        return null;
    }

    @Override
    public ElementChanges updateView(Component component, ElementChanges elementChanges) {
        if (this.modelBinding != null) {
            BindingResult bindingResult = this.modelBinding.getBindingResult();
            if (bindingResult != null) {
                String newValue = component.convertBindingValueToString(bindingResult);
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
       String stringAttributeValue = valueChange.getStringAttribute(getXmlValue());
        if (stringAttributeValue != null && !Objects.equals(stringAttributeValue, this.value)) {
            this.value = stringAttributeValue;
            updateBindingForValue(stringAttributeValue, this.modelBinding, this.value);
        }
    }
}
