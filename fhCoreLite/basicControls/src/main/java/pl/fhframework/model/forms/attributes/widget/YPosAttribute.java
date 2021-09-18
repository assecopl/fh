package pl.fhframework.model.forms.attributes.widget;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attributes.AbstractBoundableIntegerAttribute;

@DocumentedComponentAttribute(value = "Y position of widget", type = Integer.class, defaultValue = "1", boundable = true)
@XMLProperty(YPosAttribute.POSITION_Y_ATTR)
public class YPosAttribute extends AbstractBoundableIntegerAttribute {

    public static final String POSITION_Y_ATTR = "posY";

    public YPosAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return POSITION_Y_ATTR;
    }
    
}
