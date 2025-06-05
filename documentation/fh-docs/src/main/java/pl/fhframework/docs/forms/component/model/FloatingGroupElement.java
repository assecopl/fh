package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.model.forms.attributes.FloatingGroupStateAttribute;
import pl.fhframework.core.designer.ComponentElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FloatingGroupElement extends ComponentElement {
    private FloatingGroupStateAttribute.FloatingState floatingState = FloatingGroupStateAttribute.FloatingState.PINNED_MINIMIZED;
    private FloatingGroupStateAttribute.FloatingState boundFloatingState = FloatingGroupStateAttribute.FloatingState.PINNED_MINIMIZED;
    private FloatingGroupStateAttribute.FloatingState notDraggableFloatingState = FloatingGroupStateAttribute.FloatingState.PINNED_MINIMIZED;
}
