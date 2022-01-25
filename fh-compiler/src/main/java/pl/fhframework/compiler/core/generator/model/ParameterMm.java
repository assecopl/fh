package pl.fhframework.compiler.core.generator.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;

@Getter
@Setter
@NoArgsConstructor
public class ParameterMm {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Parameter parameter;

    public ParameterMm(Parameter parameter) {
        this.parameter = parameter;
    }

    @JsonGetter
    public String getName() {
        return parameter.getName();
    }

    @JsonGetter
    public ExpressionMm getValue() {
        return new ExpressionMm(parameter.getValue());
    }
}
