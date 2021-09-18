package pl.fhframework.model.forms.validation;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.validation.impl.*;
import pl.fhframework.validation.ValidationManager;
import pl.fhframework.validation.Validator;

/**
 * Class responsible for creating ValidationManager should be used only as Factory. Here developers
 * can create custom validation chains for Form Components. Use this factory or some other but use
 * it as spring component, because validation logic is based on messages service component (i18n).
 */
@Component
public class ValidationFactory implements InitializingBean {

    private static ValidationFactory validationFactory;

    @Autowired
    private IsRequired isRequired;

    @Autowired
    private IsValidConversion isValidConversion;

    @Autowired
    private ValidationRule validationRule;

    @Autowired
    private CheckBoxIsRequired checkBoxIsRequired;

    @Autowired
    private RadioOptionIsRequired radioOptionIsRequired;

    @Autowired
    private RegExRequired regExRequired;

    @Autowired
    private FiledUploadRequired filedUploadRequired;

    @Autowired
    private ComboIsRequired comboIsRequired;

    @Autowired
    private SelectComboIsRequired selectComboIsRequired;

    /**
     * Simple validation manager for basic BaseInputField.
     */
    public ValidationManager<BaseInputField> getBasicValidationProcess() {
        ValidationManager<BaseInputField> vm = new ValidationManager<>(false);
        vm.addValidator(isRequired)
                .addValidator(validationRule)
                .addValidator(isValidConversion);

        return vm;
    }

    /**
     * Validation manager for CheckBox component.
     */
    public ValidationManager<BaseInputField> getCheckBoxValidationProcess() {
        ValidationManager<BaseInputField> vm = new ValidationManager<>(false);
        vm.addValidator(checkBoxIsRequired)
                .addValidator(validationRule);

        return vm;
    }

    public ValidationManager<BaseInputField> getInputTextValidationProcess() {
        ValidationManager<BaseInputField> vm = new ValidationManager<>(false);
        vm.addValidator(isRequired)
                .addValidator(regExRequired)
                .addValidator(validationRule)
                .addValidator(isValidConversion);
        return vm;
    }

    public ValidationManager<FileUpload> getFileUploadValidationProcess() {
        ValidationManager<FileUpload> vm = new ValidationManager<>(false);
        vm.addValidator(filedUploadRequired);
        return vm;
    }

    public ValidationManager<Combo> getComboValidationProcess() {
        ValidationManager<Combo> vm = new ValidationManager<>();
        vm.addValidator(comboIsRequired)
                .addValidator((Validator)isValidConversion);
        return vm;
    }

    public ValidationManager<SelectComboMenu> getSelectComboValidationProcess() {
        ValidationManager<SelectComboMenu> vm = new ValidationManager<>();
        vm.addValidator(selectComboIsRequired)
                .addValidator((Validator)isValidConversion);
        return vm;
    }

    public ValidationManager<BaseInputField> getRadioOptionValidationProcess() {
        ValidationManager<BaseInputField> vm = new ValidationManager<>();
        vm.addValidator(radioOptionIsRequired);
        return vm;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        validationFactory = this;
    }

    public static ValidationFactory getInstance() {
        return validationFactory;
    }
}
