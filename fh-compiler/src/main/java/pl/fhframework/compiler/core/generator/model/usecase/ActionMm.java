package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Action;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Finish;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Permission;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Start;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.Run;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ActionMm<T extends Action> extends Element<T> {

    public ActionMm(T element, UseCaseMm parent) {
        super(element, parent);
    }

    @JsonGetter
    public List<ParameterDefinition> getParameterDefinitions() {
        return element.getParameterDefinitions();
    }

    @JsonGetter
    public List<CommandMm> getCommands() {
        return element.getCommands().stream().map(command -> CommandMm.getInstance(command, parent)).collect(Collectors.toList());
    }

    @JsonGetter
    public List<Permission> getPermissions() {
        return element.getPermissions();
    }

    @JsonIgnore
    public List<RunMm> getOutRunLinks() {
        return filterLinks(Run.class, Run::getSourceId).
                stream().map(link -> new RunMm(link, parent, false)).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<ActionLinkMm> getOutEventsLinks() {
        return filterLinks(ActionLink.class, ActionLink::getSourceId).
                stream().map(link -> new ActionLinkMm(link, parent, false)).collect(Collectors.toList());
    }

    public static ActionMm getInstance(Action action, UseCaseMm parent) {
        if (action instanceof Start) {
            return new StartMm((Start) action, parent);
        }
        if (action instanceof Finish) {
            return new FinishMm((Finish) action, parent);
        }
        return new ActionMm(action, parent);
    }
}
