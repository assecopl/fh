package pl.fhframework.compiler.core.uc.dynamic.model.element.detail;

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

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@XmlRootElement(name = "Exit")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class UseCaseExit implements Child<RunUseCase>, Linkable<RunUseCase>, ISnapshotEnabled, Cloneable {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String from;

    @XmlTransient
    private String name;

    @XmlAttribute
    private String to;

    @XmlElements({
            @XmlElement(name = "Parameter", type = Parameter.class)
    })
    @XmlElementWrapper(name = "Parameters")
    private List<Parameter> parameters = new LinkedList<>();

    @XmlAttribute
    private String sourcePort;

    @XmlAttribute
    private String targetPort;

    @XmlAttribute
    private String vertices;

    @Getter
    @Setter
    @XmlTransient
    @JsonIgnore
    private RunUseCase parent;

    @XmlTransient
    @JsonIgnore
    private UseCaseElement target;

    public UseCaseExit(UseCaseExit other) {
        this.id = other.id;
        this.from = other.from;
        this.name = other.name;
        this.to = other.to;
        this.sourcePort = other.sourcePort;
        this.targetPort = other.targetPort;

        if (other.parameters != null) {
            other.parameters.forEach(parameter -> this.parameters.add((Parameter) parameter.clone()));
        }

        this.vertices = other.vertices;
    }

    @Override
    public Object clone() {
        return new UseCaseExit(this);
    }

    @Override
    public RunUseCase getParent() {
        return parent;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getSourceId() {
        return getParent().getId();
    }

    @Override
    public String getSourcePort() {
        return sourcePort;
    }

    @Override
    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    @Override
    public String getTargetId() {
        return to;
    }

    @Override
    public void setTargetId(String targetId) {
        this.to = targetId;
    }

    @Override
    public String getTargetPort() {
        return targetPort;
    }

    @Override
    public void setTargetPort(String targetPort) {
        this.targetPort = targetPort;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LinkTypeEnum getType() {
        return LinkTypeEnum.OnExit;
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UseCaseExit that = (UseCaseExit) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(from, that.from)) return false;
        if (!Objects.equals(to, that.to)) return false;
        if (!Objects.equals(sourcePort, that.sourcePort)) return false;
        if (!Objects.equals(targetPort, that.targetPort)) return false;
        if (!Objects.equals(vertices, that.vertices)) return false;
        return Objects.equals(getSourceId(), that.getSourceId());
    }

    @Override
    public int hashCode() {
        int result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (to != null ? to.hashCode() : 0);
        result = 31 * result + (sourcePort != null ? sourcePort.hashCode() : 0);
        result = 31 * result + (targetPort != null ? targetPort.hashCode() : 0);
        result = 31 * result + (getSourceId() != null ? getSourceId().hashCode() : 0);
        return result;
    }
}
