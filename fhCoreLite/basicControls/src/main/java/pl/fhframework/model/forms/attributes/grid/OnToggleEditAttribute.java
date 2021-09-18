package pl.fhframework.model.forms.attributes.grid;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attributes.AbstractStringAttribute;

@DocumentedComponentAttribute("Action invoked when edit mode is performed")
@XMLProperty(OnToggleEditAttribute.ON_TOGGLE_EDIT_ATTR)
public class OnToggleEditAttribute extends AbstractStringAttribute {
    public static final String ON_TOGGLE_EDIT_ATTR = "onToggleEdit";

    public OnToggleEditAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return ON_TOGGLE_EDIT_ATTR;
    }
}
