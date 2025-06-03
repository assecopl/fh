package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.core.dynamic.DynamicClassName;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventSignatureInfo {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private ActionSignature actionSignature;

    public EventSignatureInfo(ActionSignature actionSignature) {
        this.actionSignature = actionSignature;
    }

    @JsonGetter
    public String getActionName() {
        return actionSignature.getActionName();
    }

    @JsonGetter
    public List<ParameterDefinition> getArguments() {
        final List<ParameterDefinition> arguments = new LinkedList<>();

        Arrays.stream(actionSignature.getArgumentTypes()).forEach(type -> {
            VariableType varType = VariableType.of(type);
            ParameterDefinition parameter = varType.asParameterDefinition();
            parameter.setName(JavaNamesUtils.getFieldName(DynamicClassName.forClassName(varType.getBaseType()).getBaseClassName()) + (varType.isCollection() ? "List" : "") + (arguments.size() + 1));
            arguments.add(parameter);
        });

        return arguments;
    }

}
