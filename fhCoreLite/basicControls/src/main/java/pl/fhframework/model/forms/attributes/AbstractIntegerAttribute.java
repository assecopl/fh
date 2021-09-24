package pl.fhframework.model.forms.attributes;

import pl.fhframework.BindingResult;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

public abstract class AbstractIntegerAttribute extends Attribute<Integer> {

    protected Integer value;

    public AbstractIntegerAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component);
        if (modelBinding != null) {
            BindingResult bindingResult = modelBinding.getBindingResult();
            if (bindingResult != null) {
                this.value = component.convertValue(bindingResult.getValue(), Integer.class);
            }
        } else {
            value = getDefaultValue();
        }
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getDefaultValue() {
        return 0;
    }
}
