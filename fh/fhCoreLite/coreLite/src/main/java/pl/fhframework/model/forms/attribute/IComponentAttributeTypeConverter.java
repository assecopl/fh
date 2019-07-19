package pl.fhframework.model.forms.attribute;

import pl.fhframework.model.forms.Component;

import java.util.Optional;

/**
 * Component's attribute type converter. Implementations MUST be stateless.
 */
public interface IComponentAttributeTypeConverter<T> {

    /**
     * Convert value from XML to attribute's value
     * @param owner owner component
     * @param value value from XML
     * @return object to be set to attribute
     */
    public T fromXML(Component owner, String value);

    /**
     * Convert value from attribute to XML value
     * @param ownerClass owner component class
     * @param value value from attribute
     * @return XML value
     */
    public String toXML(Class<? extends Component> ownerClass, T value);

    default Optional<String> toJavaLiteral(Component owner, T value) {
        return Optional.empty();
    }
}
