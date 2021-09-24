package pl.fhframework.model.forms.attributes.widget;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attributes.AbstractBoundableIntegerAttribute;

@DocumentedComponentAttribute(value = "Y size of widget", type = Integer.class, defaultValue = "0", boundable = true)
@XMLProperty(SizeYAttribute.SIZE_Y_ATTR)
public class SizeYAttribute extends AbstractBoundableIntegerAttribute {

    public static final String SIZE_Y_ATTR = "sizeY";

    public SizeYAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return SIZE_Y_ATTR;
    }

    @Override
    public Integer getDefaultValue() {
        return 1;
    }
}
