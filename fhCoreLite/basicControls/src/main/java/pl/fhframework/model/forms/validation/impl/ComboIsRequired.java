package pl.fhframework.model.forms.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.BindingResult;
import pl.fhframework.model.forms.Combo;
import pl.fhframework.model.forms.validation.Constant;
import pl.fhframework.validation.ConstraintViolation;
import pl.fhframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

@Component
public class ComboIsRequired implements Validator<Combo> {

    @Autowired
    private MessageService messageService;

    @Override
    public List<ConstraintViolation<Combo>> validate(Combo combo) {
        List<ConstraintViolation<Combo>> violations = new ArrayList<>();

        if (!combo.isRequired()) {
            return violations;
        }

        BindingResult selectedBindingResult = combo.getModelBinding().getBindingResult();
        if (isValueEmptyInBinding(selectedBindingResult)) {
            addConstraintViolation(violations, combo, messageService.getAllBundles().getMessage(Constant.FIELD_IS_REQUIRED_KEY));
        }
        return violations;
    }

    private boolean isValueEmptyInBinding(BindingResult bindingResult) {
        return bindingResult.getValue() == null || "".equals(bindingResult.getValue().toString());
    }

}
