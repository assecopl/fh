package pl.fhframework.model.forms.attributes.widget;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attributes.AbstractBoundableIntegerAttribute;

@DocumentedComponentAttribute(value = "X position of widget", type = Integer.class, defaultValue = "1", boundable = true)
@XMLProperty(XPosAttribute.POSITION_X_ATTR)
public class XPosAttribute extends AbstractBoundableIntegerAttribute {

    public static final String POSITION_X_ATTR = "posX";

    public XPosAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return POSITION_X_ATTR;
    }


}
