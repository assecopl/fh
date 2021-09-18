package pl.fhframework.model.forms.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.BindingResult;
import pl.fhframework.model.forms.BaseInputField;
import pl.fhframework.model.forms.InputText;
import pl.fhframework.model.forms.validation.Constant;
import pl.fhframework.validation.ConstraintViolation;
import pl.fhframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegExRequired implements Validator<BaseInputField> {

    @Autowired
    private MessageService messageService;

    @Override
    public List<ConstraintViolation<BaseInputField>> validate(BaseInputField baseInputField) {
        List<ConstraintViolation<BaseInputField>> violations = new ArrayList<>();
        InputText inputText = (InputText) baseInputField;
        if (StringUtils.isNullOrEmpty(inputText.getRequiredRegex())) {
            return violations;
        }

        BindingResult bindingResult = inputText.getModelBinding().getBindingResult();
        Object value = bindingResult != null ? bindingResult.getValue() : null;
        if (value != null) {
            String text = value.toString();
            if (!text.isEmpty() && !inputText.getRequiredRegexPattern().matcher(text).matches()) {
                String message = null;
                if (inputText.getRequiredRegexMessage() != null) {
                    BindingResult<String> msgBindingResult = inputText.getRequiredRegexMessage().getBindingResult();
                    message = msgBindingResult.getValue();
                }
                if (message == null) {
                    message = messageService.getAllBundles().getMessage(Constant.FIELD_REGEX_REQUIRED_KEY, new String[]{inputText.getRequiredRegex()});
                }
                addConstraintViolation(violations, inputText, message);
            }
        }
        return violations;
    }
}
