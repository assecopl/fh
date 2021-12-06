package pl.fhframework.compiler.core.uc.dynamic.model.element;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Child;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.uc.dynamic.model.element.behaviour.Identifiable;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Start.class, Finish.class, Action.class, RunSubUseCase.class, RunUseCase.class, StoreAccess.class})
@XmlType(propOrder = {"id", "label", "description"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class UseCaseElement implements Child<Flow>, Executable, Identifiable, ISnapshotEnabled, Cloneable {

    @XmlAttribute
    protected String label;

    @XmlAttribute
    protected String description;

    @Getter
    @XmlAttribute
    protected String id;

    @XmlTransient
    @JsonIgnore
    protected Flow parent;

    @XmlAttribute
    protected Integer posX;

    @XmlAttribute
    protected Integer posY;

    @XmlAttribute
    protected ActionSizeEnum size;

    public abstract UseCaseElement copy();

    @Override
    public Object clone() {
        return this.copy();
    }

    @Override
    public String getName() {
        return label;
    }
}
