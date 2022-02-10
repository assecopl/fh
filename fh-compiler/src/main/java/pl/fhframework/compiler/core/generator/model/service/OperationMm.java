package pl.fhframework.compiler.core.generator.model.service;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.model.Wrapper;
import pl.fhframework.compiler.core.services.dynamic.model.Operation;
import pl.fhframework.compiler.core.services.dynamic.model.RestProperties;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.rules.dynamic.model.Statement;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class OperationMm implements Wrapper {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Operation operation;

    public OperationMm(Operation operation) {
        this.operation = operation;
    }

    @JsonGetter
    public String getId(){
        return operation.getRule().getId();
    }

    @JsonGetter
    public String getLabel(){
        return operation.getRule().getLabel();
    }

    @JsonGetter
    public List<ParameterDefinition> getInputParams() {
        return operation.getRule().getInputParams();
    }

    @JsonGetter
    public List<Statement> getBody() {
        return operation.getRule().getRuleDefinition() != null ? operation.getRule().getRuleDefinition().getStatements() : Collections.emptyList();
    }

    @JsonGetter
    public ParameterDefinition getOutputParam() {
        return operation.getRule().getOutputParams() != null && !operation.getRule().getOutputParams().isEmpty() ? operation.getRule().getOutputParams().get(0) : null;
    }

    @JsonGetter
    public RestProperties getRestProperties() {
        return operation.getRestProperties();
    }

    @Override
    public <T> T provideImpl() {
        return (T) operation;
    }
}
