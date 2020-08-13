package pl.fhframework.core.rules.builtin;

import pl.fhframework.model.forms.AccessibilityEnum;

/**
 * Availiablity utility
 */
public class AvailabilityUtils {

    public AccessibilityEnum availabilityHideIf(boolean condition) {
        return condition ? AccessibilityEnum.HIDDEN : AccessibilityEnum.EDIT;
    }

    public AccessibilityEnum availabilityHideIfElseView(boolean condition) {
        return condition ? AccessibilityEnum.HIDDEN : AccessibilityEnum.EDIT;
    }

    public AccessibilityEnum availabilityViewIf(boolean condition) {
        return condition ? AccessibilityEnum.VIEW : AccessibilityEnum.EDIT;
    }

    public AccessibilityEnum availabilitySum(AccessibilityEnum one, AccessibilityEnum two) {
        return one.sumAccessibility(two);
    }
}
