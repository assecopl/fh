package pl.fhframework.model.forms.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.model.forms.BaseInputField;
import pl.fhframework.model.forms.validation.Constant;
import pl.fhframework.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckBoxIsRequired extends IsRequired {

    @Autowired
    private MessageService messageService;

    @Override
    public List<ConstraintViolation<BaseInputField>> validate(BaseInputField value) {
        List<ConstraintViolation<BaseInputField>> violations = new ArrayList<>();

        boolean canBeValidated = super.canProcessValidation(value);
        if (canBeValidated
                && Boolean.FALSE.equals(value.getModelBinding().getBindingResult().getValue())) {
            addConstraintViolation(violations, value, messageService.getAllBundles().getMessage(Constant.FIELD_IS_REQUIRED_KEY));
        }
        return violations;
    }
}
