package pl.fhframework.model.forms.attributes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.fhframework.Binding;
import pl.fhframework.BindingResult;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

public abstract class Attribute<T> {

    @JsonIgnore
    private static final Binding BINDING = new Binding();

    public abstract T getValue();

    public abstract void setValue(T value);

    public abstract String getXmlValue();

    public abstract T getDefaultValue();

    protected String convertBindingToString(BindingResult bindingResult) {
        return BINDING.convertBindingValueToString(bindingResult);
    }

    public Attribute(Form form, Component component) {
    }
}
