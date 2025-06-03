package pl.fhframework.compiler.core.generator.model.form;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.model.forms.Property;

@Getter
@Setter
@NoArgsConstructor
public class PropertyMm {
    @JsonIgnore
    private Property property;

    public PropertyMm(Property property) {
        this.property = property;
    }

    @JsonGetter
    public String getName() {
        return property.getName();
    }

    @JsonGetter
    public String getType() {
        return property.getTypeAsDisplayed();
    }

    @JsonGetter
    public Property.PropertyMultiplicity getMultiplicity() {
        return property.getMultiplicity();
    }
}
