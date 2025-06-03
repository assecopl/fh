package pl.fhframework.compiler.core.uc.dynamic.model.element.attribute;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;

import javax.xml.bind.annotation.*;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Parameter")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Parameter implements ISnapshotEnabled, Cloneable {
    @XmlAttribute
    private String name;

    @XmlAttribute
    private String value;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private VariableType valueType;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private String valueTypeErr;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private VariableType expectedType;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private String expectedTypeErr;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private String comment;

    private Parameter(Parameter other) {
        this.name = other.name;
        this.value = other.value;
        this.valueType = other.valueType;
        this.expectedType = other.expectedType;
        this.valueTypeErr = other.valueTypeErr;
        this.expectedTypeErr = other.expectedTypeErr;
        this.comment = other.comment;
    }

    @Override
    public Object clone() {
        return new Parameter(this);
    }

    public void setValue(String value) {
        this.value = value;
        this.valueType = null;
    }

    public ParameterDefinition asParameterDefinition() {
        ParameterDefinition parameterDefinition = expectedType.asParameterDefinition();
        parameterDefinition.setName(getName());

        return parameterDefinition;
    }

    public static Parameter of(String name, String value){
        Parameter parameter = new Parameter();
        parameter.setName(name);
        parameter.setValue(value);

        return parameter;
    }
}
