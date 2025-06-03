package pl.fhframework.compiler.core.generator.model.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.model.meta.AttributeTag;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;

@Getter
@Setter
@NoArgsConstructor
public class AttributeMm {
    @JsonIgnore
    private AttributeTag attribute;

    public AttributeMm(AttributeTag attribute) {
        this.attribute = attribute;
    }

    @JsonGetter
    public String getName() {
        return attribute.getName();
    }

    @JsonGetter
    public String getType() {
        return attribute.getType();
    }

    @JsonGetter
    public TypeMultiplicityEnum getMultiplicity() {
        return attribute.getMultiplicity();
    }

    @JsonGetter
    public String getJsonPropertyName() {
        return attribute.getJsonProperty();
    }
}
