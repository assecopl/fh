package pl.fhframework.model.forms.attribute;

import pl.fhframework.annotations.XMLPropertyGlobalConverter;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.Component;

/**
 * Created by Piotr on 2017-02-09.
 */
@XMLPropertyGlobalConverter(PresentationStyleEnum.class)
public class PresentationStyleAttrConverter implements IComponentAttributeTypeConverter<PresentationStyleEnum> {

    @Override
    public PresentationStyleEnum fromXML(Component owner, String value) {
        if (value == null) {
            return null;
        } else {
            return Enum.valueOf(PresentationStyleEnum.class, value.toUpperCase());
        }
    }

    @Override
    public String toXML(Class<? extends Component> ownerClass, PresentationStyleEnum value) {
        return value != null ? value.name().toLowerCase() : null;
    }
}
