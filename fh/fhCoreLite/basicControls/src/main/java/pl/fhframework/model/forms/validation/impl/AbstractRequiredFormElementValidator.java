package pl.fhframework.model.forms.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;

import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.model.forms.validation.Constant;
import pl.fhframework.validation.ConstraintViolation;
import pl.fhframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRequiredFormElementValidator<T> implements Validator<T> {

    @Autowired
    private MessageService messageService;

    public abstract boolean canProcessValidation(T formElement);

    public abstract boolean isNotValid(T formElement);

    @Override
    public List<ConstraintViolation<T>> validate(T formElement) {
        List<ConstraintViolation<T>> violations = new ArrayList<>();

        if (!canProcessValidation(formElement)) {
            return violations;
        }

        if (isNotValid(formElement)) {
            addConstraintViolation(violations, formElement, messageService.getAllBundles().getMessage(Constant.FIELD_IS_REQUIRED_KEY));
        }
        return violations;
    }


}
