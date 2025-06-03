package pl.fhframework.compiler.core.uc.dynamic.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;

import java.io.Serializable;

/**
 * Created by Pawel.Ruta on 2017-06-08.
 */
@Getter
@Setter
@AllArgsConstructor
public class VariableContext implements Serializable {
    private String name;

    private VariableType variableType;

    private boolean passedAsParameter;

    public VariableContext(String name, VariableType variableType) {
        this.name = name;
        this.variableType = variableType;
    }

    public static VariableContext of(ParameterDefinition parameterDefinition) {
        return new VariableContext(parameterDefinition.getName(), VariableType.of(parameterDefinition));
    }
}
