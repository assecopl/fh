package pl.fhframework.docs.converter;

import pl.fhframework.docs.converter.model.ConverterDocumentationModel;
import pl.fhframework.annotations.AvailabilityRuleMethod;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.AccessibilityRule;
import pl.fhframework.model.forms.Form;

/**
 * Created by Amadeusz Szkiladz on 12.12.2016.
 */
public class ConverterDocumentationForm extends Form<ConverterDocumentationModel> {

    @AvailabilityRuleMethod("boundUserLabel1")
    protected AccessibilityEnum isBoundUserLabel1Visible(AccessibilityRule accessibilityRule) {
        return isGroupActive(accessibilityRule);
    }

    @AvailabilityRuleMethod("boundUserLabel2")
    protected AccessibilityEnum isBoundUserLabel2Visible(AccessibilityRule accessibilityRule) {
        return isGroupActive(accessibilityRule);
    }
    @AvailabilityRuleMethod("boundUserLabel3")
    protected AccessibilityEnum isBoundUserLabel3Visible(AccessibilityRule accessibilityRule) {
        return isGroupActive(accessibilityRule);
    }

    private AccessibilityEnum isGroupActive(AccessibilityRule accessibilityRule) {
        if (getModel().getTypedUser()!=null)
            return AccessibilityEnum.EDIT;
        else
            return AccessibilityEnum.HIDDEN;
    }

}
