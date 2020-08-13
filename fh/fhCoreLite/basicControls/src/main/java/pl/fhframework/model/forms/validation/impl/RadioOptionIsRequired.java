package pl.fhframework.model.forms.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.model.forms.BaseInputField;
import pl.fhframework.model.forms.RadioOption;
import pl.fhframework.model.forms.SelectOneMenu;
import pl.fhframework.model.forms.validation.Constant;
import pl.fhframework.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;

@Component
public class RadioOptionIsRequired extends IsRequired {

    @Override
    public List<ConstraintViolation<BaseInputField>> validate(BaseInputField value) {
        return validate(value, ((RadioOption)value).getGroupModelBinding());
    }
}
