package pl.fhframework.model.forms.attribute;

import pl.fhframework.core.FhCL;
import pl.fhframework.model.forms.Component;

/**
 * Component attribute converter for classes.
 */
public class ClassByFullNameAttrConverter implements IComponentAttributeTypeConverter<Class<?>> {

    @Override
    public Class<?> fromXML(Component owner, String value) {
        if (value == null) {
            return null;
        } else {
            try {
                return FhCL.classLoader.loadClass(value);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Cannot find class passed as XML attribute: " + value);
            }
        }
    }

    @Override
    public String toXML(Class<? extends Component> ownerClass, Class<?> value) {
        if (value == null) {
            return null;
        } else {
            return value.getName();
        }
    }
}
