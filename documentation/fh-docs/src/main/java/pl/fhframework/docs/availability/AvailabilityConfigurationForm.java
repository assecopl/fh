package pl.fhframework.docs.availability;

import pl.fhframework.docs.availability.model.AvailabilityConfigurationModel;
import pl.fhframework.annotations.AvailabilityRuleMethod;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.AccessibilityRule;
import pl.fhframework.model.forms.Form;

public class AvailabilityConfigurationForm extends Form<AvailabilityConfigurationModel> {

    @AvailabilityRuleMethod("setByProgrammerExampleText1")
    protected AccessibilityEnum setByProgrammerExampleText1(AccessibilityRule accessibilityRule) {
        return AccessibilityEnum.VIEW;
    }

    @AvailabilityRuleMethod("setByProgrammerExampleText2")
    protected AccessibilityEnum setByProgrammerExampleText2(AccessibilityRule accessibilityRule) {
        return AccessibilityEnum.VIEW;
    }

    @AvailabilityRuleMethod("setByProgrammerExampleText3")
    protected AccessibilityEnum setByProgrammerExampleText3(AccessibilityRule accessibilityRule) {
        return AccessibilityEnum.VIEW;
    }

    public AccessibilityEnum calculatedAvailability(boolean someParameter) {
        if (someParameter) {
            return AccessibilityEnum.VIEW;
        } else {
            return AccessibilityEnum.EDIT;
        }
    }
}
