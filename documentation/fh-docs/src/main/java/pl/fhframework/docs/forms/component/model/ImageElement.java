package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ImageElement extends ComponentElement {
    private String mapSrc = "images/location.jpg";
    private String onClickedMessage = "Component clicked 0 times.";
    private String onAreaClickedMessage = "Component clicked 0 times.";
    private String map = "empty";

    public void incrementCounter() {
        counter++;
    }

    public void incrementAreaCounter() {
        counterArea++;
    }

    private int counter = 0;
    private int counterArea = 0;

}
