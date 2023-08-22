package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.designer.ComponentElement;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by mateusz.zaremba
 */
@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WizardElement extends ComponentElement {
    private int boundActiveTabId = 0;
    private boolean showOptionalTab = false;
}


