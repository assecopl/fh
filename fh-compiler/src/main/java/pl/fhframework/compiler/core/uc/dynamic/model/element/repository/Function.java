package pl.fhframework.compiler.core.uc.dynamic.model.element.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Executable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Repository;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Child;
import pl.fhframework.core.uc.dynamic.model.element.behaviour.Identifiable;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "CallFunction")
@XmlAccessorType(XmlAccessType.FIELD)
public class Function implements Child<Repository>, Executable, Identifiable, ISnapshotEnabled, Cloneable {
    @XmlAttribute
    private String name;

    @XmlAttribute
    protected String description;

    @XmlElements({
            @XmlElement(name = "ParameterDefinition", type = ParameterDefinition.class)
    })
    @XmlElementWrapper(name = "ParameterDefinitions")
    private List<ParameterDefinition> parameterDefinitions = new LinkedList<>();

    @XmlTransient
    @JsonIgnore
    protected Repository parent;

    private Function(Function other) {
        this.name = other.name;
        this.description = other.description;
        if (!CollectionUtils.isEmpty(other.parameterDefinitions)) {
            other.parameterDefinitions.forEach(parameter -> this.parameterDefinitions.add((ParameterDefinition) parameter.clone()));
        }
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public void setId(String id) {
        this.name = id;
    }

    @Override
    public Object clone() {
        return new Function(this);
    }
}
