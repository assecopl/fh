package pl.fhframework.core.logging.processor;

import pl.fhframework.core.logging.ErrorInformation;

/**
 * Created by Adam Zareba on 30.01.2017.
 */
public interface IErrorInformationProcessor {

    boolean process(ErrorInformation errorInformation);

    default boolean isTypeOf(Throwable exception, Class<? extends Throwable> throwableClass) {
        if (exception != null) {
            if (throwableClass.isInstance(exception)) {
                return true;
            } else {
                return isTypeOf(exception.getCause(), throwableClass);
            }
        } else {
            return false;
        }
    }
}
