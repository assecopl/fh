package pl.fhframework.model.forms.attributes;

import pl.fhframework.BindingResult;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

/**
 * Created by szkiladza on 02.02.2017.
 */
public abstract class AbstractStringAttribute extends Attribute<String> {

    private String value;

    public AbstractStringAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component);
        if (modelBinding != null) {
            BindingResult bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {
                this.value = convertBindingToString(bindingResult);
            }
        } else {
            value = getDefaultValue();
        }
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getDefaultValue() {
        return "";
    }
}
