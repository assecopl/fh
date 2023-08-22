package pl.fhframework.docs.validation.forms;

import pl.fhframework.docs.validation.forms.model.ValidationInfoModel;
import pl.fhframework.annotations.AvailabilityRuleMethod;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.AccessibilityRule;
import pl.fhframework.model.forms.Form;

public class ValidationInfoForm extends Form<ValidationInfoModel> {

    public static final String DESCRIPTION_GROUP = "descriptionGroup";
    public static final String EXAMPLE_GROUP_SIMPLE = "exampleGroupSimple";
    public static final String EXAMPLE_GROUP_JSR = "exampleGroupJsr";
    public static final String EXAMPLE_GROUP_COMBINED = "exampleGroupCombined";
    public static final String EXAMPLE_GROUP_VALIDATION_RULE = "exampleGroupValidationRule";
    public static final String EXAMPLE_GROUP_ALETRNATE = "exampleGroupAlternate";


    @AvailabilityRuleMethod(DESCRIPTION_GROUP)
    protected AccessibilityEnum getDescGroupAccess(AccessibilityRule accessibilityRule) {
        return checkActiveTab(0);
    }

    @AvailabilityRuleMethod(EXAMPLE_GROUP_SIMPLE)
    protected AccessibilityEnum getSimpleGroupAccess(AccessibilityRule accessibilityRule) {
        return checkActiveTab(1);
    }

    @AvailabilityRuleMethod(EXAMPLE_GROUP_VALIDATION_RULE)
    protected AccessibilityEnum getValidationRuleGroupAccess(AccessibilityRule accessibilityRule) {
        return checkActiveTab(1);
    }

    @AvailabilityRuleMethod(EXAMPLE_GROUP_JSR)
    protected AccessibilityEnum getJsrGroupAccess(AccessibilityRule accessibilityRule) {
        return checkActiveTab(2);
    }

    @AvailabilityRuleMethod(EXAMPLE_GROUP_COMBINED)
    protected AccessibilityEnum getCombinedGroupAccess(AccessibilityRule accessibilityRule) {
        return checkActiveTab(3);
    }

    @AvailabilityRuleMethod(EXAMPLE_GROUP_ALETRNATE)
    protected AccessibilityEnum getCombinedAlernate(AccessibilityRule accessibilityRule) {
        return checkActiveTab(4);
    }

    private AccessibilityEnum checkActiveTab(int tabId) {
        if (getModel().getActiveTabId() == tabId) {
            return AccessibilityEnum.EDIT;
        } else {
            return AccessibilityEnum.VIEW;
        }
    }

}
