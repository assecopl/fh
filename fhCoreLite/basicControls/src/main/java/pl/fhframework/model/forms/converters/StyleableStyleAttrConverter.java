package pl.fhframework.model.forms.converters;

import pl.fhframework.annotations.XMLPropertyGlobalConverter;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Styleable.Style;
import pl.fhframework.model.forms.attribute.IComponentAttributeTypeConverter;

import java.util.Optional;

/**
 * Created by Piotr on 2017-02-02.
 */
@XMLPropertyGlobalConverter(Style.class)
public class StyleableStyleAttrConverter implements IComponentAttributeTypeConverter<Style> {

    @Override
    public Style fromXML(Component owner, String value) {
        if (value == null) {
            return null;
        } else {
            return Style.forValue(value);
        }
    }

    @Override
    public String toXML(Class<? extends Component> ownerClass, Style value) {
        if (value == null) {
            return null;
        } else {
            return value.toValue();
        }
    }

    @Override
    public Optional<String> toJavaLiteral(Component owner, Style value) {
        return Optional.of(Style.class.getName().replace("$", ".") + "." + value.name());
    }
}
