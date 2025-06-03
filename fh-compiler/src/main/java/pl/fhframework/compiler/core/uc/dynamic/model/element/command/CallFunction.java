package pl.fhframework.compiler.core.uc.dynamic.model.element.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "CallFunction")
@XmlAccessorType(XmlAccessType.FIELD)
public class CallFunction extends Command {
    @XmlAttribute
    private String ref;

    @XmlAttribute
    protected String description;

    @XmlElements({
            @XmlElement(name = "Parameter", type = Parameter.class)
    })
    @XmlElementWrapper(name = "InnerParameters")
    private List<Parameter> innerParameters = new LinkedList<>();

    @XmlElements({
            @XmlElement(name = "Parameter", type = Parameter.class)
    })
    @XmlElementWrapper(name = "Parameters")
    private List<Parameter> parameters = new LinkedList<>();

    @XmlElementRef
    private Rule rule;

    @XmlTransient
    private String parametersDescription;

    private CallFunction(CallFunction other) {
        super(other);
        this.ref = other.ref;
        this.description = other.description;
        if (!CollectionUtils.isEmpty(other.parameters)) {
            other.parameters.forEach(parameter -> this.parameters.add((Parameter) parameter.clone()));
        }
    }

    @Override
    public ActivityTypeEnum getActivityType() {
        return Optional.ofNullable(getPredefinedFunctionType()).orElse(ActivityTypeEnum.RunRule);
    }

    @Override
    public String getTargetName() {
        return ref;
    }

    public String getShortName() {
        if (rule != null) {
            return "Internal rule";
        }
        else {
            String longName = getInnerParameters().get(0).getValue();
            if (StringUtils.isNullOrEmpty(longName)) {
                return "";
            }
            String shortClassName = DynamicClassName.forClassName(DynamicClassName.forClassName(longName).getPackageName()).getBaseClassName();
            String shmethod = DynamicClassName.forClassName(longName).getBaseClassName();
            return shortClassName+"."+shmethod;
        }
    }
    @Override
    public Object clone() {
        return new CallFunction(this);
    }

    public ActivityTypeEnum getPredefinedFunctionType() {
        return Arrays.stream(ActivityTypeEnum.values()).filter(activityTypeEnum -> activityTypeEnum.name().equals(ref)).findFirst().orElse(null);
    }
}
