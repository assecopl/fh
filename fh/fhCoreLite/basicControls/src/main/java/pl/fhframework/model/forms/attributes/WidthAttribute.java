package pl.fhframework.model.forms.attributes;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

@DocumentedComponentAttribute("Component width in px")
@XMLProperty(WidthAttribute.WIDTH_ATTR)
public class WidthAttribute extends AbstractStringAttribute {
    public static final String WIDTH_ATTR = "width";

    public WidthAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return WIDTH_ATTR;
    }
}
