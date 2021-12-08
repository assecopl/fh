package pl.fhframework.model.forms.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.BindingResult;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.model.forms.ComboFhDP;
import pl.fhframework.validation.ConstraintViolation;
import pl.fhframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

@Component
public class ComboFhDPIsRequired implements Validator<ComboFhDP> {

    @Autowired
    private MessageService messageService;

    @Override
    public List<ConstraintViolation<ComboFhDP>> validate(ComboFhDP combo) {
        List<ConstraintViolation<ComboFhDP>> violations = new ArrayList<>();

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
