package pl.fhframework.model.forms.attributes;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

@DocumentedComponentAttribute(value = "Defines if group is fullScreen or not", type = Boolean.class, defaultValue = "false", boundable = true)
@XMLProperty(FullScreenAttribute.FULL_SCREEN_ATTR)
public class FullScreenAttribute extends BoundableBoleanAttribute {

    public static final String FULL_SCREEN_ATTR = "fullScreen";

    public FullScreenAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return FULL_SCREEN_ATTR;
    }

}
