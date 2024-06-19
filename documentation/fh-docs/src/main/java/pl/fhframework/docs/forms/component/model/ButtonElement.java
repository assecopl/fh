package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.docs.forms.service.StylesService;
import pl.fhframework.model.forms.Styleable;
import pl.fhframework.core.designer.ComponentElement;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ButtonElement extends ComponentElement {
    private int counter = 0;
    private int counterArea = 0;
    private String onClickedMessage = "Component clicked 0 times.";
    private String onAreaClickedMessage = "Component clicked 0 times.";
    private String onChangedMessage = "Component changed 0 times.";
    private String map = "empty";
    private int fizzBuzzCounter = 0;
    private List<Styleable.Style> styles;
    private String selectedStyle = Styleable.Style.PRIMARY.toValue();
    private Styleable.Style selectedEnumStyle = Styleable.Style.PRIMARY;
    private int styleIndex = 0;

    public ButtonElement (StylesService service) {
        styles = service.getStyles();
        styleIndex = styles.indexOf(selectedEnumStyle);
    }

    public void incrementCounter() {
        counter++;
    }

    public void incrementAreaCounter() {
        counterArea++;
    }

    public String getButtonLabel() {
        if (fizzBuzzCounter % 15 == 0) {
            return "FizzBuzz";
        } else if (fizzBuzzCounter % 3 == 0) {
            return "Fizz";
        } else if (fizzBuzzCounter % 5 == 0) {
            return "Buzz";
        } else {
            return String.valueOf(fizzBuzzCounter);
        }
    }

    public void fizzBuzz() {
        fizzBuzzCounter++;
    }
}
