package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Control(parents = {Form.class})
public class LocaleBundle extends FormElement {

    @JsonIgnore
    @XMLProperty(required = true)
    @DocumentedComponentAttribute(value = "Message source bean name.")
    private String basename;

    @JsonIgnore
    @XMLProperty(required = true)
    @DocumentedComponentAttribute(value = "Prefix for messages used in xml form.")
    private String var;

    public LocaleBundle(Form form) {
        super(form);
    }

}