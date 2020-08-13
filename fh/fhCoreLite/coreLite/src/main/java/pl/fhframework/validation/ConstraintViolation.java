package pl.fhframework.validation;

/**
 * Constraint violation
 * <p>
 * Created by Krzysztof Noga on 2016-12-14.
 *
 * @param <T>
 */
public interface ConstraintViolation<T> {

    /**
     * @return message describing the violation
     */
    String getMessage();

    /**
     * @return the value of which did not pass validation
     */
    T getInvalidValue();
}
