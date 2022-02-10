package pl.fhframework.compiler.core.uc.dynamic.model.element.attribute;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Action;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Start;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Child;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.uc.meta.ParameterInfo;

import javax.xml.bind.annotation.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "ParameterDefinition")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ParameterDefinition implements Child<Action>, ISnapshotEnabled, Cloneable {

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private TypeMultiplicityEnum multiplicity = TypeMultiplicityEnum.Element;

    @XmlAttribute
    private String comment;

    @Getter
    @Setter
    @XmlTransient
    @JsonIgnore
    private Action parent;

    @XmlTransient
    @JsonIgnore
    private boolean primitive;

    public ParameterDefinition(String type, String name, TypeMultiplicityEnum multiplicity) {
        this(type, name, multiplicity, false);
    }

    public ParameterDefinition(String type, String name, TypeMultiplicityEnum multiplicity, boolean primitive) {
        this.type = type;
        this.name = name;
        this.multiplicity = multiplicity;
        this.primitive = primitive;
    }

    protected ParameterDefinition(ParameterDefinition other) {
        this.type = other.type;
        this.name = other.name;
        this.multiplicity = other.multiplicity;
    }

    @Override
    public Object clone() {
        return new ParameterDefinition(this);
    }

    @Override
    @JsonIgnore
    public Action getParent() {
        return parent;
    }

    public ParameterKindEnum getKind() {
        if (getParent() instanceof Start) {
            return ParameterKindEnum.Input;
        }

        return ParameterKindEnum.Output;
    }

    @JsonIgnore
    public boolean isCollection() {
        return multiplicity == TypeMultiplicityEnum.Collection;
    }

    @JsonIgnore
    public boolean isPageable() {
        return multiplicity == TypeMultiplicityEnum.MultiplePageable;
    }

    public boolean sameType(ParameterDefinition parameter) {
        return Objects.equals(type, parameter.type) && multiplicity == parameter.multiplicity;
    }

    @JsonIgnore
    public String getTypeName() {
        String baseType = DynamicClassName.forClassName(type).getBaseClassName();

        if (isCollection()) {
            return baseType.concat(" [1..*]");
        }
        return baseType;
    }

    @JsonIgnore
    public String getBaseTypeName() {
        return DynamicClassName.forClassName(type).getBaseClassName();
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public static ParameterInfo toParameterInfo(ParameterDefinition pd) {
        return new ParameterInfo(pd.getType(), pd.getName(), pd.getMultiplicity(), pd.isPrimitive());
    }

    public static ParameterDefinition fromParameterInfo(ParameterInfo pi) {
        return new ParameterDefinition(pi.getType(), pi.getName(), pi.getMultiplicity(), pi.isPrimitive());
    }
}
