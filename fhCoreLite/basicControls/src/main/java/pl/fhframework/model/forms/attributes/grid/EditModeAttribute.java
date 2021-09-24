package pl.fhframework.model.forms.attributes.grid;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attributes.BoundableBoleanAttribute;

@DocumentedComponentAttribute(value = "Defines if grid is in edit mode", type = Boolean.class, defaultValue = "false", boundable = true)
@XMLProperty(EditModeAttribute.EDIT_MODE_ATTR)
public class EditModeAttribute extends BoundableBoleanAttribute {

    public static final String EDIT_MODE_ATTR = "editMode";

    public EditModeAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return EDIT_MODE_ATTR;
    }

}
