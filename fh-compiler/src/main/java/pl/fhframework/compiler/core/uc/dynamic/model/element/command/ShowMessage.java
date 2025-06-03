package pl.fhframework.compiler.core.uc.dynamic.model.element.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Parental;
import pl.fhframework.event.dto.NotificationEvent;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "ShowMessage")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ShowMessage extends ShowForm implements Parental {
    @XmlAttribute
    private Type type;

    @XmlAttribute
    private NotificationEvent.Level severity = NotificationEvent.Level.INFO;

    @XmlAttribute
    private String title;

    @XmlAttribute
    private String message;

    // value is label, name is action name
    @XmlElementWrapper(name = "Buttons")
    @XmlElements({
            @XmlElement(name = "Button", type = Parameter.class)
    })
    private List<Parameter> actionButtons = new LinkedList<>();

    public enum Type {
        Notification,
        Dialog
    }

    @Override
    public boolean isInteractive() {
        return type == Type.Dialog;
    }

    @Override
    public ActivityTypeEnum getActivityType() {
        return ActivityTypeEnum.ShowMessage;
    }

    @Override
    public String getTargetName() {
        return type == null ? "" : type.name();
    }

    @Override
    public List<Parameter> getParameters() {
        return new LinkedList<>(getActionButtons());
    }

    @Override
    public String getForm() {
        return getTargetName();
    }

    @Override
    public void setForm(String form) {
        throw new UnsupportedOperationException("ShowMessage doesn't have external form");
    }
}
