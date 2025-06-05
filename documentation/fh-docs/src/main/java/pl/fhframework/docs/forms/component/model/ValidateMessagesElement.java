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
public class ValidateMessagesElement extends ComponentElement {

    private String selectOneMenuValueToValidation = "";
    private String selectOneMenuCitizenshipToValidation = "";
    private String inputDateRequiredToValidation = "";

    private String selectOneMenuValueToValidationNonNavigable = "";
    private String selectOneMenuPersonToValidationNonNavigable = "";
    private String requiredValue = "";
    private String inputDateRequiredToValidationNonNavigable = "";

    private boolean state = true;
}
