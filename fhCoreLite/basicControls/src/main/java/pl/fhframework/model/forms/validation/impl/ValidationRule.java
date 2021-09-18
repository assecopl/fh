package pl.fhframework.model.forms.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.model.forms.BaseInputField;
import pl.fhframework.model.forms.validation.Constant;
import pl.fhframework.validation.ConstraintViolation;
import pl.fhframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

@Component
public class ValidationRule implements Validator<BaseInputField> {

    @Autowired
    private MessageService messageService;

    public boolean canProcessValidation(Object object) {
        BaseInputField baseInputField = (BaseInputField) object;
        String validationRule = baseInputField.getValidationRule();
        return !StringUtils.isEmpty(validationRule) && (validationRule).startsWith("-");
    }

    @Override
    public List<ConstraintViolation<BaseInputField>> validate(BaseInputField value) {
        List<ConstraintViolation<BaseInputField>> violations = new ArrayList<>();

        if (!canProcessValidation(value)) {
            return violations;
        }

        String validationRule = value.getValidationRule();
        String skipFirstChar = validationRule.substring(1);
        if (!value.getForm().expressionResult(skipFirstChar)) {
            String constraint = skipFirstChar.replace(" lt ", "<").replace(" eq ", "=").replace(" gt ", ">");
            Object[] args = {constraint};
            addConstraintViolation(violations, value, messageService.getAllBundles().getMessage(Constant.FIELD_NOT_VALID_KEY, args));
        }
        return violations;
    }
}
