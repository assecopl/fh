package pl.fhframework.model.forms.attributes;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

@DocumentedComponentAttribute(value =
        "This attribute is possible to use with binding value. If binding value is present, pressing button will" +
                " be visible as switching it on and off. But when this attribute is not provided, this" +
                " clicking want leave any visible mark.",
        type = Boolean.class, defaultValue = "null", boundable = true)
@XMLProperty(ClickedAttribute.CLICKED_ATTR)
public class ClickedAttribute extends BoundableBoleanAttribute {

    public static final String CLICKED_ATTR = "clicked";

    public ClickedAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return CLICKED_ATTR;
    }

    @Override
    public Boolean getDefaultValue() {
        return null;
    }
}
