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
public class CheckBoxElement extends ComponentElement {
    private boolean checkBoxOnChangeValue;
    private boolean checkBoxToCheckBoxValue;
    private boolean checkBoxValue = true;
}
