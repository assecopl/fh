package pl.fhframework.model.forms;

/**
 * Definition of types of component accessibility
 */
public enum AccessibilityEnum {

    DEFECTED, HIDDEN, VIEW, EDIT; //TODO: It is worth to add last, default and always highest state - INHERITED, which means that form component just inherites value. Eventually, we can assume that null value is this kind of state

    public AccessibilityEnum sumAccessibility(AccessibilityEnum otherAccessibility) {
        if (otherAccessibility == null) return this;
        if (this.ordinal() < otherAccessibility.ordinal()) return this;
        else return otherAccessibility;
    }
}
