package pl.fhframework.model.forms.attributes.clock;

import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attributes.BoundableStringAttribute;

/**
 * Created by k.czajkowski on 16.02.2017.
 */
@XMLProperty(DayNamesAttribute.DAY_NAMES_ATTR)
@DocumentedComponentAttribute("List of names.")
public class DayNamesAttribute extends BoundableStringAttribute {

    public static final String DAY_NAMES_ATTR = "dayNames";

    public DayNamesAttribute(Form form, Component component, ModelBinding modelBinding) {
        super(form, component, modelBinding);
    }

    @Override
    public String getXmlValue() {
        return DAY_NAMES_ATTR;
    }

    @Override
    public String getDefaultValue() {
        return "";
    }
}
