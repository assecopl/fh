package pl.fhframework.model.forms.attributes;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;

@DocumentedComponentAttribute(value = "Defines if group is pinned or not", type = Boolean.class, defaultValue = "true", boundable = true)
@XMLProperty(PinnedAttribute.PINNED_ATTR)
public class PinnedAttribute extends BoundableBoleanAttribute {

    public static final String PINNED_ATTR = "pinned";

    public PinnedAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return PINNED_ATTR;
    }

    @Override
    public Boolean getDefaultValue() {
        return Boolean.TRUE;
    }
}
