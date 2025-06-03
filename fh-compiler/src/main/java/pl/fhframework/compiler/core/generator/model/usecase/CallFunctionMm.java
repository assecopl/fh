package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.generator.model.ParameterMm;
import pl.fhframework.compiler.core.generator.model.rule.RuleMm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ActivityTypeEnum;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.CallFunction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class CallFunctionMm extends CommandMm<CallFunction> {
    public CallFunctionMm(CallFunction command, UseCaseMm parent) {
        super(command, parent);
    }

    @JsonGetter
    ActivityTypeEnum getFunction() {
        return command.getActivityType();
    }

    @JsonGetter
    public String getDescription() {
        return command.getDescription();
    }

    @JsonGetter
    public List<ParameterMm> getParameters() {
        if (isWithParameters()) {
            return command.getParameters().stream().map(ParameterMm::new).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @JsonGetter
    public ExpressionMm getExpression() {
        if (!isWithParameters() && !command.getParameters().isEmpty()) {
            return new ExpressionMm(command.getParameters().get(0).getValue());
        }
        return null;
    }

    @JsonGetter
    public RuleMm getRule() {
        if (command.getRule() != null) {
            return new RuleMm(command.getRule(), parent.getDependencies(), parent.getMetadata());
        }
        return null;
    }

    @JsonGetter
    public String getRuleId() {
        if (command.getActivityType() == ActivityTypeEnum.RunRule) {
            return getRuleServiceId();
        }
        return null;
    }

    @JsonGetter
    public String getServiceId() {
        if (command.getActivityType() == ActivityTypeEnum.RunService) {
            return getRuleServiceId();
        }
        return null;
    }

    @JsonIgnore
    private String getRuleServiceId() {
        Optional<Parameter> idParam = command.getInnerParameters().stream().filter(parameter -> "ruleId".equals(parameter.getName())).findFirst();
        return idParam.map(Parameter::getValue).orElse(null);

    }

    @JsonIgnore
    private boolean isWithParameters() {
        return Arrays.asList(ActivityTypeEnum.RunRule, ActivityTypeEnum.RunService, ActivityTypeEnum.ShowForm, ActivityTypeEnum.ShowMessage).contains(getActivityType());
    }
}
