package pl.fhframework.core.uc.dynamic.model.element.attribute;

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

    @Override
    public String toString() {
        return name();
    }
}