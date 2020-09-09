package pl.fhframework.model.forms.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.BindingResult;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.BaseInputField;
import pl.fhframework.model.forms.validation.Constant;
import pl.fhframework.validation.ConstraintViolation;
import pl.fhframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

@Component
public class IsRequired implements Validator<BaseInputField> {

    @Autowired
    private MessageService messageService;

    protected static final String STRING_EMPTY = "";

    public boolean canProcessValidation(BaseInputField baseInputField) {

        if(baseInputField.getRequiredBinding() != null && baseInputField.getRequiredBinding().getBindingResult() != null){
            return baseInputField.getRequiredBinding().getBindingResult().getValue();
        }
        return baseInputField.isRequired();
    }

    @Override
    public List<ConstraintViolation<BaseInputField>> validate(BaseInputField value) {
        return validate(value, value.getModelBinding());
    }

    protected List<ConstraintViolation<BaseInputField>> validate(BaseInputField value, ModelBinding modelBinding) {
        List<ConstraintViolation<BaseInputField>> violations = new ArrayList<>();

        if (!canProcessValidation(value)) {
            return violations;
        }

        BindingResult bindingResult = modelBinding.getBindingResult();
        if (bindingResult.getValue() == null || STRING_EMPTY.equals(bindingResult.getValue().toString())) {
            addConstraintViolation(violations, value, messageService.getAllBundles().getMessage(Constant.FIELD_IS_REQUIRED_KEY));
        }
        return violations;
    }
}
