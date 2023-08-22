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
public class RadioOptionsGroupElement extends ComponentElement {
    private String selectRadioGroupValue = "Poland";
    private String selectRadioGroupValue2 = "";
    private String selectRadioGroupOnChangeValue = "Poland";
    private String radioGroupToRadioGroupValue = "Poland";
}
