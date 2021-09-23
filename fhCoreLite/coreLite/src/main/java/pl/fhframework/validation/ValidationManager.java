package pl.fhframework.validation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manager to support validation
 * <p>
 * Created by Krzysztof Noga on 2016-12-14.
 *
 * @param <T>
 */
public class ValidationManager<T> {

    private final boolean stopOnFirstHandlerException;

    private List<Validator<T>> validators = new ArrayList<>();

    /**
     * The default constructor. Validation is to stop on the first violation
     */
    public ValidationManager() {
        this.stopOnFirstHandlerException = true;
    }

    /**
     * @param stopOnFirstHandlerException - true if validation is to stop on the first violation. default true
     */
    public ValidationManager(boolean stopOnFirstHandlerException) {
        this.stopOnFirstHandlerException = stopOnFirstHandlerException;
    }

    private void validate(final T value, final Iterator<Validator<T>> iterator, final List<ConstraintViolation<T>> violationsToReturn) {
        if (iterator.hasNext()) {
            Validator<T> validator = iterator.next();
            List<ConstraintViolation<T>> violations = validator.validate(value);
            checkViolations(violations, value, iterator, violationsToReturn);
        }
    }

    private void checkViolations(final List<ConstraintViolation<T>> violations, final T value, final Iterator<Validator<T>> iterator,
                                 final List<ConstraintViolation<T>> violationsToReturn) {
        if (violations != null && violations.size() > 0) {
            violationsToReturn.addAll(violations);
            /*
             * validation error
			 */
            if (stopOnFirstHandlerException) {
                return;
            } else {
                validate(value, iterator, violationsToReturn);
            }
        } else {
            /*
             * validation ok
			 */
            validate(value, iterator, violationsToReturn);
        }
    }

    /**
     * Adds validator to the list
     *
     * @param validator
     * @return return ValidationManager
     */
    public ValidationManager<T> addValidator(Validator<T> validator) {
        validators.add(validator);
        return this;
    }

    /**
     * Validates the specified value.
     * The value is checked by all validators,
     * or interrupted for the first violation when stopOnFirstHandlerException=true
     *
     * @param value - value to validation
     * @return list of violations. If size &gt; 0, the validation does not successfully
     */
    public List<ConstraintViolation<T>> validate(T value) {
        final Iterator<Validator<T>> iterator = validators.iterator();
        List<ConstraintViolation<T>> violationsToReturn = new ArrayList<>();
        validate(value, iterator, violationsToReturn);

        return violationsToReturn;
    }

}
