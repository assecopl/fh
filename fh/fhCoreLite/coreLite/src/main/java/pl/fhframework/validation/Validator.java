package pl.fhframework.validation;

import java.util.List;

/**
 * Validator interface
 * <p>
 * Created by Krzysztof Noga on 2016-12-14.
 *
 * @param <T>
 */
public interface Validator<T> {

    /**
     * @param violations - list of violations
     * @param value      - value validated
     * @param message    - the message violation
     */
    default void addConstraintViolation(final List<ConstraintViolation<T>> violations, final T value, final String message) {
        violations.add(new ConstraintViolation<T>() {

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public T getInvalidValue() {
                return value;
            }
        });
    }

    /**
     * @param value - value to validation
     * @return list of violations. If size > 0, the validation does not successfully
     */
    List<ConstraintViolation<T>> validate(T value);

}
