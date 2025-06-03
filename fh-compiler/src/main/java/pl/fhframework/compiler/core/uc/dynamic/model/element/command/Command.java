package pl.fhframework.compiler.core.uc.dynamic.model.element.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableContext;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Action;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.WithParameters;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({Run.class, CallFunction.class, ShowForm.class})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public abstract class Command implements WithParameters<Action>, ISnapshotEnabled, Cloneable{
    @XmlAttribute
    private String condition;

    @XmlAttribute
    private String returnHolder;

    @XmlAttribute
    private boolean localVariable;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private VariableType returnType;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private String returnTypeErr;

    @Getter
    @Setter
    @XmlTransient
    @JsonIgnore
    private Action parent;

    public Command(Command other) {
        this.condition = other.condition;
        this.returnHolder = other.returnHolder;
        this.localVariable = other.localVariable;
    }

    public abstract Object clone();

    public void setReturnHolder(String returnHolder) {
        this.returnHolder = returnHolder;
        returnType = null;
    }

    @SkipSnapshot
    public VariableContext getLocalVariableDefinition() {
        if (isLocalVariable()) {
            return new VariableContext(getReturnHolder(), getReturnType());
        }

        return null;
    }
}
