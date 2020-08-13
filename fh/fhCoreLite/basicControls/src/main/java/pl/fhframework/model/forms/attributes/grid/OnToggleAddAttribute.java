package pl.fhframework.model.forms.attributes.grid;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attributes.AbstractStringAttribute;

@DocumentedComponentAttribute("Action invoked when add widget action is performed")
@XMLProperty(OnToggleAddAttribute.ON_TOGGLE_ADD_ATTR)
public class OnToggleAddAttribute extends AbstractStringAttribute {
    public static final String ON_TOGGLE_ADD_ATTR = "onToggleAdd";

    public OnToggleAddAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return ON_TOGGLE_ADD_ATTR;
    }
}
