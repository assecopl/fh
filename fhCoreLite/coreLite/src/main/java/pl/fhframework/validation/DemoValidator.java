package pl.fhframework.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by Gabriel.Kurzac on 2016-09-19.
 */
public class DemoValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target == null){
            errors.reject("123");
        }
    }
}
