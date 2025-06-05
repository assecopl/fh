package pl.fhframework.docs.rule;

import pl.fhframework.core.rules.AccessibilityRule;
import pl.fhframework.model.forms.AccessibilityEnum;

/**
 * Created by pawel.ruta on 2017-05-29.
 */
@AccessibilityRule
public class AccessibilityRuleExample {
    public AccessibilityEnum isGroupVisible(boolean visible, pl.fhframework.model.forms.AccessibilityRule accessibilityRule) {
        if (visible) {
            return AccessibilityEnum.EDIT;
        }

        return AccessibilityEnum.HIDDEN;
    }
}
