package pl.fhframework.model.forms.attributes;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

@DocumentedComponentAttribute(value = "Defines if group will be in floating mode only", type = Boolean.class, defaultValue = "false", boundable = true)
@XMLProperty(FloatingOnlyAttribute.FLOATING_ONLY_ATTR)
public class FloatingOnlyAttribute extends BoundableBoleanAttribute {

    public static final String FLOATING_ONLY_ATTR = "floatingOnly";

    public FloatingOnlyAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return FLOATING_ONLY_ATTR;
    }

}
