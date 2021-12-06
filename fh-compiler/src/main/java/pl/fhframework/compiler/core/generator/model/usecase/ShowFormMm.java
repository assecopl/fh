package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.generator.model.ParameterMm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ShowForm;
import pl.fhframework.binding.ActionSignature;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ShowFormMm<T extends ShowForm> extends CommandMm<T> {
    public ShowFormMm(ShowForm showForm, UseCaseMm parent) {
        super((T) showForm, parent);
    }

    @JsonGetter
    public FormInfo getForm() {
        return new FormInfo(command, parent.getMetadata().getMetadata(command.getForm()));
    }

    @JsonGetter
    public String getVariant() {
        return command.getVariant();
    }

    @JsonGetter
    public List<ActionLinkMm> getActionLinks() {
        return command.getActionLinks().stream().map(link -> new ActionLinkMm(link, parent, false)).collect(Collectors.toList());
    }

    @JsonGetter
    public ExpressionMm getModelProperty(){
        return command.getModelParameter() == null ? null : new ExpressionMm(command.getModelParameter().getValue());
    }

    @JsonGetter
    public List<ParameterMm> getFormDataElements() {
        return command.getFormDataElements().stream().map(ParameterMm::new).collect(Collectors.toList()); // todo: model type ?
    }

    @JsonGetter
    public Map<String, ActionSignature> getNotAddedEvents() {
        return parent.getNotAddedEvents().entrySet().stream().
                filter(entry -> Objects.equals(entry.getKey().getValue(), command.getForm())).
                map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey().getKey(), entry.getValue())).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
