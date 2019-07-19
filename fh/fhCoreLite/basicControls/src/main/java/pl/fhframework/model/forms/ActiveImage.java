package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.annotations.Control;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.CallbackActionBinding;
import pl.fhframework.binding.IActionCallback;
import pl.fhframework.binding.IActionCallbackContext;
import pl.fhframework.model.dto.InMessageEventData;

import java.util.Optional;

import static pl.fhframework.annotations.DesignerXMLProperty.PropertyFunctionalArea.BEHAVIOR;

/**
 * Class representing xml component of ActiveImage. Every field represents xml attribute of
 * activeImage tag.
 * <p>
 * Example <ActiveImage src="value_1" onClick=""/>.
 * <p>
 * Every field is parsed as json for javascript. If field should be ingored by JSON, use
 * <code>@JsonIgnore</code>. There can be used any annotations for json generator.
 */
@Getter
@Setter
@Control(parents = {PanelGroup.class, Column.class, Tab.class, Row.class, Form.class, Group.class})
public class ActiveImage extends PanelGroup {
    public static final String ATTR_SRC = "src";
    public static final String ATTR_ON_CLICK = "onClick";

    @Getter
    @Setter
    @XMLProperty
    private String src;

    @Getter
    @XMLProperty
    @DesignerXMLProperty(functionalArea = BEHAVIOR)
    private ActionBinding onClick;

    public ActiveImage(Form form) {
        super(form);
    }

    @Override
    public Optional<ActionBinding> getEventHandler(InMessageEventData eventData) {
        if (eventData.getEventType().equals(ATTR_ON_CLICK)) {
            return Optional.ofNullable(onClick);
        } else {
            return super.getEventHandler(eventData);
        }
    }

    public void setOnClick(ActionBinding onClick) {
        this.onClick = onClick;
    }

    public IActionCallbackContext setOnClick(IActionCallback onClick) {
        return CallbackActionBinding.createAndSet(onClick, this::setOnClick);
    }
}
