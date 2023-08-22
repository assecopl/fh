package pl.fhframework.docs.rule;

import pl.fhframework.core.rules.ValidationRule;
import pl.fhframework.core.rules.ValidationRuleResult;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.validation.Constant;
import pl.fhframework.validation.ValidationRuleBase;

/**
 * Created by pawel.ruta on 2017-05-29.
 */
@ValidationRule
public class ValidationRuleExample extends ValidationRuleBase {
    public void validateFieldIsEmpty(String input) {
        if (StringUtils.isNullOrEmpty(input)) {
            addCustomMessage("pole", Constant.FIELD_IS_REQUIRED_KEY, PresentationStyleEnum.ERROR);
        }
    }
}
