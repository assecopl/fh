package pl.fhframework.compiler.core.uc.dynamic.model.element.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Child;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Parental;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "ShowForm")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ShowMessage.class})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ShowForm extends Command implements Parental {
    @XmlAttribute
    private String form;

    @XmlAttribute
    private String variant;

    @XmlElements({
            @XmlElement(name = "ActionLink", type = ActionLink.class)
    })
    private List<ActionLink> actionLinks = new LinkedList<>();

    @XmlElementWrapper(name = "FormData")
    @XmlElements({
            @XmlElement(name = "Element", type = Parameter.class)
    })
    private List<Parameter> formDataElements = new LinkedList<>();

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private VariableType modelValueType;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private String modelValueTypeErr;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private Parameter modelParameter = new Parameter();

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private VariableType modelExpectedType;

    public void addActionLink(ActionLink actionLink) {
        actionLinks.add(actionLink);
        actionLink.setParent(this);
    }

    public ShowForm(ShowForm other) {
        super(other);
        this.form = other.form;
        this.variant = other.variant;
        if (!CollectionUtils.isEmpty(other.actionLinks)) {
            other.actionLinks.forEach(actionLink -> this.actionLinks.add((ActionLink) actionLink.clone()));
        }
        if (!CollectionUtils.isEmpty(other.formDataElements)) {
            other.formDataElements.forEach(parameter -> this.formDataElements.add((Parameter) parameter.clone()));
        }
    }

    @XmlAttribute
    public String getModel() {
        return getModelParameter().getValue();
    }

    public void setModel(String model) {
        getModelParameter().setValue(model);
        this.modelValueType = null;
    }

    @Override
    public boolean removeChild(Child child) {
        ((ActionLink) child).setParent(null);
        return actionLinks.remove(child);
    }

    @Override
    public void addChild(Child child) {
        addActionLink((ActionLink) child);
    }

    @Override
    public String getName() {
        return form;
    }

    @Override
    public ActivityTypeEnum getActivityType() {
        return ActivityTypeEnum.ShowForm;
    }

    @Override
    public String getTargetName() {
        return form;
    }

    @Override
    public Object clone() {
        return new ShowForm(this);
    }

    @Override
    public List<Parameter> getParameters() {
        LinkedList<Parameter> parameters = new LinkedList<>();
        parameters.add(modelParameter);
        parameters.addAll(formDataElements);

        return parameters;
    }

    public boolean isInteractive() {
        return true;
    }
}
