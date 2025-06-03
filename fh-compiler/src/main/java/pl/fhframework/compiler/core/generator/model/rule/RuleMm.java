package pl.fhframework.compiler.core.generator.model.rule;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.MetaModel;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.rules.dynamic.model.Statement;

import java.util.Collections;
import java.util.List;

// todo:
@Getter
@Setter
@NoArgsConstructor
public class RuleMm extends MetaModel {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Rule rule;

    public RuleMm(Rule rule, List<String> dependencies, ModuleMetaModel metadata) {
        setDependencies(dependencies);
        this.rule = rule;
    }

    @JsonGetter
    public String getId() {
        return rule.getId();
    }

    @JsonGetter
    public List<ParameterDefinition> getInputParams() {
        return rule.getInputParams();
    }

    @JsonGetter
    public List<Statement> getBody() {
        return rule.getRuleDefinition() != null ? rule.getRuleDefinition().getStatements() : Collections.emptyList();
    }

    @JsonGetter
    public ParameterDefinition getOutputParam() {
        return rule.getOutputParams() != null && !rule.getOutputParams().isEmpty() ? rule.getOutputParams().get(0) : null;
    }

    @Override
    public <T> T provideImpl() {
        return (T) rule;
    }
}
