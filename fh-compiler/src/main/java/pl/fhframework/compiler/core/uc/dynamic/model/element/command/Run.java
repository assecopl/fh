package pl.fhframework.compiler.core.uc.dynamic.model.element.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.*;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Run")
@XmlAccessorType(XmlAccessType.FIELD)
public class Run extends Command implements Linkable<Action> {
    @XmlAttribute
    protected String id;

    @XmlAttribute
    private String ref;

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
    private String vertices;

    @XmlTransient
    @JsonIgnore
    private UseCaseElement target;

    public Run(Run other) {
        super(other);
        this.id = other.id;
        this.ref = other.ref;
        if (!CollectionUtils.isEmpty(other.parameters)) {
            other.parameters.forEach(parameter -> this.parameters.add((Parameter) parameter.clone()));
        }
        this.sourcePort = other.sourcePort;
        this.targetPort = other.targetPort;
        this.vertices = other.vertices;
    }

    @Override
    public Object clone() {
        return new Run(this);
    }

    public Run(Executable executable) {
        this.setRef(executable.getId());
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
        return getParent().getId();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public LinkTypeEnum getType() {
        return LinkTypeEnum.Run;
    }

    @Override
    public ActivityTypeEnum getActivityType() {
        if (RunUseCase.class.isInstance(getTarget())) {
            return ActivityTypeEnum.RunUseCase;
        }
        if (StoreAccess.class.isInstance(getTarget())) {
            return ActivityTypeEnum.DataRead; // todo: DataRead remove
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Run run = (Run) o;

        if (!Objects.equals(id, run.id)) return false;
        if (!Objects.equals(ref, run.ref)) return false;
        if (!Objects.equals(getSourceId(), run.getSourceId())) return false;
        if (!Objects.equals(sourcePort, run.sourcePort)) return false;
        if (!Objects.equals(vertices, run.vertices)) return false;
        return Objects.equals(targetPort, run.targetPort);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ref != null ? ref.hashCode() : 0);
        result = 31 * result + (getSourceId() != null ? getSourceId().hashCode() : 0);
        result = 31 * result + (sourcePort != null ? sourcePort.hashCode() : 0);
        result = 31 * result + (targetPort != null ? targetPort.hashCode() : 0);
        return result;
    }
}