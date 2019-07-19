package pl.fhframework.core.uc.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterInfo {

    private String type;

    private String name;

    private TypeMultiplicityEnum multiplicity = TypeMultiplicityEnum.Element;

    private boolean primitive;

    public boolean isCollection() {
        return multiplicity == TypeMultiplicityEnum.Collection;
    }

    public boolean isPageable() {
        return multiplicity == TypeMultiplicityEnum.MultiplePageable;
    }

    public boolean sameType(ParameterInfo parameter) {
        return Objects.equals(type, parameter.type) && multiplicity == parameter.multiplicity;
    }

    public String getTypeName() {
        String baseType = DynamicClassName.forClassName(type).getBaseClassName();

        if (isCollection()) {
            return baseType.concat(" [1..*]");
        }
        if (isPageable()) {
            return baseType.concat(" {page}");
        }
        return baseType;
    }

    public String getBaseTypeName() {
        return DynamicClassName.forClassName(type).getBaseClassName();
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }
}
