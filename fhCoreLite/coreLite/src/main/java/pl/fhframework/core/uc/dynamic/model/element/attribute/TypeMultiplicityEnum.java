package pl.fhframework.core.uc.dynamic.model.element.attribute;

import pl.fhframework.core.FhException;
import pl.fhframework.model.forms.Property;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ParameterMultiplicity")
@XmlEnum
public enum TypeMultiplicityEnum {
    Element,
    Collection,
    MultiplePageable,
    ;

    public static TypeMultiplicityEnum fromValue(String v) {
        return valueOf(v);
    }

    public static TypeMultiplicityEnum from(Property.PropertyMultiplicity multiplicity) {
        switch (multiplicity) {
            case SINGLE:
                return Element;
            case MULTIPLE:
                return Collection;
            case MULTIPLE_PAGEABLE:
                return MultiplePageable;
            default:
                throw new FhException("Unknown multiplicity " + multiplicity.name());
        }
    }

    @Override
    public String toString() {
        return name();
    }


}
