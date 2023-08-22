package pl.fhframework.docs.forms.component;

import pl.fhframework.docs.forms.component.model.WizardElement;
import pl.fhframework.annotations.AvailabilityRuleMethod;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.AccessibilityRule;
import pl.fhframework.model.forms.Form;

/**
 * Created by mateusz.zaremba
 */
public class WizardForm extends Form<WizardElement> {



    @AvailabilityRuleMethod("wizardOptionalTab")
    protected AccessibilityEnum setByProgrammerExampleText1(AccessibilityRule accessibilityRule) {
        return getModel().isShowOptionalTab() ? AccessibilityEnum.EDIT : AccessibilityEnum.HIDDEN;
    }
}
