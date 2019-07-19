package pl.fhframework.model.forms.attributes.widget;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attributes.AbstractBoundableIntegerAttribute;

@DocumentedComponentAttribute(value = "X size of widget", type = Integer.class, defaultValue = "1", boundable = true)
@XMLProperty(SizeXAttribute.SIZE_X_ATTR)
public class SizeXAttribute extends AbstractBoundableIntegerAttribute {

    public static final String SIZE_X_ATTR = "sizeX";

    public SizeXAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return SIZE_X_ATTR;
    }

    @Override
    public Integer getDefaultValue() {
        return 1;
    }
}
