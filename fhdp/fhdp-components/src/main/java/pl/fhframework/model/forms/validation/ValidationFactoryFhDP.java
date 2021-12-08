package pl.fhframework.model.forms.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.model.forms.ComboFhDP;
import pl.fhframework.model.forms.validation.impl.IsValidConversion;
import pl.fhframework.validation.ValidationManager;
import pl.fhframework.validation.Validator;

@Component
public class ValidationFactoryFhDP extends ValidationFactory {
    private static ValidationFactoryFhDP validationFactoryFhDP;

    @Autowired
    private IsValidConversion isValidConversion;
    @Autowired
    private ComboFhDPIsRequired comboFhDPIsRequired;

    public ValidationManager<ComboFhDP> getComboFhDPValidationProcess() {
        ValidationManager<ComboFhDP> vm = new ValidationManager<>();
        vm.addValidator(comboFhDPIsRequired)
                .addValidator((Validator)isValidConversion);
        return vm;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        validationFactoryFhDP = this;
    }
    public static ValidationFactoryFhDP getInstance() {
        return validationFactoryFhDP;
    }
}
