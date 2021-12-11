package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.uc.meta.UseCaseActionInfo;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ActionInfo extends ElementInfo {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private UseCaseActionInfo actionInfo;

    public ActionInfo(UseCaseActionInfo actionInfo) {
        this.actionInfo = actionInfo;
    }

    @JsonGetter
    public String getId() {
        return actionInfo.getId();
    }

    @JsonGetter
    public String getLabel() {
        return actionInfo.getName();
    }

    @JsonGetter
    public List<ParameterDefinition> getParameterDefinitions() {
        return actionInfo.getParameters().stream().map(ParameterDefinition::fromParameterInfo).collect(Collectors.toList());
    }
}
