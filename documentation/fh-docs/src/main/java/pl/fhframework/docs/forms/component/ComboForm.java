package pl.fhframework.docs.forms.component;

import pl.fhframework.docs.forms.component.model.ComboElement;
import pl.fhframework.annotations.AvailabilityRuleMethod;
import pl.fhframework.annotations.MultipleAvailabilityRuleMethods;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.AccessibilityRule;
import pl.fhframework.model.forms.Form;

public class ComboForm extends Form<ComboElement> {
    @AvailabilityRuleMethod("boundComboUserLabel1")
    protected AccessibilityEnum isBoundUserLabel1Visible(AccessibilityRule accessibilityRule) {
        return isComboUserChoosen(getModel().getSelectedCombo());
    }

    @AvailabilityRuleMethod("boundComboUserLabel2")
    protected AccessibilityEnum isBoundUserLabel2Visible(AccessibilityRule accessibilityRule) {
        return isComboUserChoosen(getModel().getSelectedCombo());
    }

    @AvailabilityRuleMethod("boundComboUserLabel3")
    protected AccessibilityEnum isBoundUserLabel3Visible(AccessibilityRule accessibilityRule) {
        return isComboUserChoosen(getModel().getSelectedCombo());
    }

    @AvailabilityRuleMethod("boundComboUserLabel4")
    protected AccessibilityEnum isBoundUserLabel4Visible(AccessibilityRule accessibilityRule) {
        return isComboUserChoosen(getModel().getSimpleSelectedCombo());
    }

    @AvailabilityRuleMethod("boundComboUserLabel5")
    protected AccessibilityEnum isBoundUserLabel5Visible(AccessibilityRule accessibilityRule) {
        return isComboUserChoosen(getModel().getSimpleSelectedCombo());
    }

    @AvailabilityRuleMethod("boundComboUserLabel6")
    protected AccessibilityEnum isBoundUserLabel6Visible(AccessibilityRule accessibilityRule) {
        return isComboUserChoosen(getModel().getSimpleSelectedCombo());
    }

    @MultipleAvailabilityRuleMethods({@AvailabilityRuleMethod("boundComboUserLabel71"), @AvailabilityRuleMethod("boundComboUserLabel72")})
    protected AccessibilityEnum isBoundUserLabel7Visible(AccessibilityRule accessibilityRule) {
        return isComboUserChoosen(getModel().getSimpleSelectedComboWithDisplayFunction());
    }

    @MultipleAvailabilityRuleMethods({@AvailabilityRuleMethod("boundComboUserLabel81"), @AvailabilityRuleMethod("boundComboUserLabel82")})
    protected AccessibilityEnum isBoundUserLabel8Visible(AccessibilityRule accessibilityRule) {
        return isComboUserChoosen(getModel().getSimpleSelectedComboWithDisplayFunction());
    }

    @MultipleAvailabilityRuleMethods({@AvailabilityRuleMethod("boundComboUserLabel91"), @AvailabilityRuleMethod("boundComboUserLabel92")})
    protected AccessibilityEnum isBoundUserLabel9Visible(AccessibilityRule accessibilityRule) {
        return isComboUserChoosen(getModel().getSimpleSelectedComboWithDisplayFunction());
    }

    private AccessibilityEnum isComboUserChoosen(Object object) {
        if (object != null)
            return AccessibilityEnum.EDIT;
        else
            return AccessibilityEnum.HIDDEN;
    }
}
