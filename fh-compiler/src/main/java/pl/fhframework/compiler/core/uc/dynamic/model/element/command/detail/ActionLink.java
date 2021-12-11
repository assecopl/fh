package pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Finish;
import pl.fhframework.compiler.core.uc.dynamic.model.element.LinkTypeEnum;
import pl.fhframework.compiler.core.uc.dynamic.model.element.RunUseCase;
import pl.fhframework.compiler.core.uc.dynamic.model.element.UseCaseElement;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Child;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ActivityTypeEnum;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ShowForm;
import pl.fhframework.events.BreakLevelEnum;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "ActionLink")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ActionLink implements Child<ShowForm>, Linkable<ShowForm>, ISnapshotEnabled, Cloneable {
    @XmlAttribute
    protected String id;
    @XmlAttribute
    private String ref;
    @XmlAttribute
    private String formAction;
    @XmlAttribute
    private String sourcePort;
    @XmlAttribute
    private String targetPort;
    @XmlElements({
            @XmlElement(name = "Parameter", type = Parameter.class)
    })
    @XmlElementWrapper(name = "Parameters")
    private List<Parameter> parameters = new LinkedList<>();

    @XmlAttribute
    private boolean validate;

    @XmlAttribute
    private boolean clearValidationContext = true;

    @XmlAttribute
    private boolean immediate = false;

    @XmlAttribute
    private boolean confirmationDialog;

    @XmlAttribute
    private String dialogTitle;

    @XmlAttribute
    private String dialogMessage;

    @XmlAttribute
    private String confirmButton;

    @XmlAttribute
    private String cancelButton;

    @XmlAttribute
    private String vertices;

    @XmlAttribute
    private BreakLevelEnum breakLevel = BreakLevelEnum.BLOCKER;

    @XmlTransient
    @JsonIgnore
    private ShowForm parent;

    @XmlTransient
    @JsonIgnore
    private UseCaseElement target;

    public ActionLink(ActionLink other) {
        this.id = other.id;
        this.ref = other.ref;
        this.formAction = other.formAction;
        this.sourcePort = other.sourcePort;
        this.targetPort = other.targetPort;
        this.vertices = other.vertices;
        this.validate =  other.validate;
        this.clearValidationContext =  other.clearValidationContext;
        this.immediate =  other.immediate;
        this.breakLevel =  other.breakLevel;
        this.confirmationDialog = other.confirmationDialog;
        this.dialogTitle = other.dialogTitle;
        this.dialogMessage = other.dialogMessage;
        this.confirmButton = other.confirmButton;
        this.cancelButton = other.cancelButton;
    }

    @Override
    @XmlTransient
    public String getTargetId() {
        return ref;
    }

    @Override
    public void setTargetId(String targetId) {
        ref = targetId;
    }

    @Override
    @XmlTransient
    public String getSourceId() {
        if (getParent() == null) {
            return null;
        }
        if (getParent().getParent() == null) {
            return null;
        }

        return getParent().getParent().getId();
    }

    @Override
    public String getName() {
        return getFormAction();
    }

    @Override
    public Object clone() {
        return new ActionLink(this);
    }

    @Override
    public LinkTypeEnum getType() {
        return LinkTypeEnum.FormEvent;
    }

    @Override
    public ActivityTypeEnum getActivityType() {
        if (RunUseCase.class.isInstance(getTarget())) {
            return ActivityTypeEnum.RunUseCase;
        }
        if (Finish.class.isInstance(getTarget())) {
            return ActivityTypeEnum.GoToExit;
        }
        return ActivityTypeEnum.RunAction;
    }

    @Override
    public String getTargetName() {
        return getTarget().getName();
    }

    @Override
    public String getCondition() {
        return null;
    }

    @Override
    public void setCondition(String string) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionLink that = (ActionLink) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(ref, that.ref)) return false;
        if (!Objects.equals(formAction, that.formAction)) return false;
        if (!Objects.equals(sourcePort, that.sourcePort)) return false;
        if (!Objects.equals(targetPort, that.targetPort)) return false;
        if (!Objects.equals(vertices, that.vertices)) return false;
        return Objects.equals(getSourceId(), that.getSourceId());
    }

    @Override
    public int hashCode() {
        int result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (ref != null ? ref.hashCode() : 0);
        result = 31 * result + (formAction != null ? formAction.hashCode() : 0);
        result = 31 * result + (sourcePort != null ? sourcePort.hashCode() : 0);
        result = 31 * result + (targetPort != null ? targetPort.hashCode() : 0);
        result = 31 * result + (getSourceId() != null ? getSourceId().hashCode() : 0);
        return result;
    }
}
