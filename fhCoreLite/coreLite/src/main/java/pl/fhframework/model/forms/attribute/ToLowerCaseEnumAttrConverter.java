package pl.fhframework.model.forms.attribute;

import lombok.Getter;
import pl.fhframework.core.FhFormException;
import pl.fhframework.model.forms.Component;

public abstract class ToLowerCaseEnumAttrConverter<E extends Enum<E>> implements IComponentAttributeTypeConverter<E> {
    @Getter
    private Class<E> enumClass;

    public ToLowerCaseEnumAttrConverter(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public E fromXML(Component owner, String value) {
        if (value == null) {
            return null;
        } else {
            try {
                return Enum.valueOf(enumClass, value.toUpperCase());
            } catch (IllegalArgumentException e) {
                try {
                    return Enum.valueOf(enumClass, value);
                } catch (IllegalArgumentException e2) {
                    throw new FhFormException("Invalid attribute value '" + value + "' for enum class " + enumClass.getSimpleName());
                }
            }
        }
    }

    @Override
    public String toXML(Class<? extends Component> ownerClass, E value) {
        if (value == null) {
            return null;
        } else {
            return value.name().toLowerCase();
        }
    }
}