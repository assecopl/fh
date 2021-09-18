package pl.fhframework.model.forms.attributes;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

@DocumentedComponentAttribute("Component height in px")
@XMLProperty(HeightAttribute.HEIGHT_ATTR)
public class HeightAttribute extends AbstractStringAttribute {
    public static final String HEIGHT_ATTR = "height";

    public HeightAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return HEIGHT_ATTR;
    }
}
