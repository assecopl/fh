package pl.fhframework.compiler.core.uc.dynamic.model.element;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Child;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Parental;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.*;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.ReflectionUtils;

import javax.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@XmlRootElement(name = "Action")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Action extends UseCaseElement implements Parental, Cloneable {

    @XmlElements({
            @XmlElement(name = "ParameterDefinition", type = ParameterDefinition.class)
    })
    @XmlElementWrapper(name = "ParameterDefinitions")
    private List<ParameterDefinition> parameterDefinitions = new LinkedList<>();

    @XmlElements({
            @XmlElement(name = "ShowForm", type = ShowForm.class),
            @XmlElement(name = "Run", type = Run.class),
            @XmlElement(name = "CallFunction", type = CallFunction.class),
            @XmlElement(name = "ShowMessage", type = ShowMessage.class)
    })
    @XmlElementWrapper(name = "Execution")
    private List<Command> commands = new LinkedList<>();

    @XmlElements({
            @XmlElement(name = "Permission", type = Permission.class)
    })
    @XmlElementWrapper(name = "Permissions")
    private List<Permission> permissions = new LinkedList<>();

    public void addCommand(Command command) {
        commands.add(command);
        command.setParent(this);
    }

    public void addCommand(int idx, Command command) {
        commands.add(idx, command);
        command.setParent(this);
    }

    public void addParameter(ParameterDefinition parameterDefinition) {
        parameterDefinition.setParent(this);
        parameterDefinitions.add(parameterDefinition);
    }

    public void removeParameter(ParameterDefinition parameterDefinition) {
        parameterDefinition.setParent(null);
        parameterDefinitions.remove(parameterDefinition);
    }

    @Override
    public boolean removeChild(Child child) {
        if (child instanceof ActionLink) {
            throw new UnsupportedOperationException("There can be more than one ShowForm");
        } else if (child instanceof ParameterDefinition) {
            removeParameter((ParameterDefinition) child);
        } else {
            if (commands != null) {
                ((Command) child).setParent(null);
                return commands.remove(child);
            }
        }
        return false;
    }

    @Override
    public void addChild(Child child) {
        if (child instanceof ActionLink) {
            throw new UnsupportedOperationException("There can be more than one ShowForm");
        } else if (child instanceof ParameterDefinition) {
            addParameter((ParameterDefinition) child);
        } else {
            addCommand((Command) child);
        }
    }

    @Override
    public UseCaseElement copy() {
        Action action = ReflectionUtils.createClassObject(this.getClass());
        if (!CollectionUtils.isEmpty(this.parameterDefinitions)) {
            this.parameterDefinitions.forEach(parameterDefinition -> action.parameterDefinitions.add((ParameterDefinition) parameterDefinition.clone()));
        }
        if (!CollectionUtils.isEmpty(this.commands)) {
            this.commands.forEach(command -> action.commands.add((Command) command.clone()));
        }
        if (!CollectionUtils.isEmpty(this.permissions)) {
            this.permissions.forEach(permission -> action.permissions.add((Permission) permission.clone()));
        }
        action.label = this.label;
        action.description = this.description;
        action.id = this.id;
        action.posX = this.posX;
        action.posY = this.posY;
        action.size = this.size;

        return action;
    }

    public ActionTypeEnum getActionType() {
        if (isInteractive()) {
            return ActionTypeEnum.Interactive;
        }

        return ActionTypeEnum.Batch;
    }

    public boolean isInteractive() {
        return (getAnyShowForm() != null);
    }

    public List<ShowForm> getShowForms() {
        return getCommands().stream().filter(ShowForm.class::isInstance).map(ShowForm.class::cast).filter(ShowForm::isInteractive).collect(Collectors.toList());
    }

    public List<ShowForm> getShowFormsDep() {
        return getCommands().stream().filter(command -> ((command instanceof ShowForm) && !(command instanceof ShowMessage))).map(ShowForm.class::cast).filter(ShowForm::isInteractive).collect(Collectors.toList());
    }

    public List<CallFunction> getCalledLocalRules() {
        return getCommands().stream().filter(CallFunction.class::isInstance).map(CallFunction.class::cast)
                .filter(cf -> Arrays.asList(ActivityTypeEnum.Validate, ActivityTypeEnum.RunRule, ActivityTypeEnum.DataRead).contains(cf.getActivityType()) && cf.getRule() != null).collect(Collectors.toList());
    }

    protected ShowForm getAnyShowForm() {
        return (ShowForm) getCommands().stream().filter(command -> command instanceof ShowForm && ((ShowForm) command).isInteractive()).findAny().orElse(null);
    }

    public List<Linkable> getLinks() {
        List links = getCommands().stream().filter(command -> command instanceof Linkable).collect(Collectors.toList());
        links.addAll(getEvents());

        return links;
    }

    public List<ActionLink> getEvents() {
        return getCommands().stream().filter(command -> command instanceof ShowForm).
                map(command -> ((ShowForm) command).getActionLinks()).flatMap(List::stream).collect(Collectors.toList());
    }
}
