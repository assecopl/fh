package pl.fhframework.core.uc;

import pl.fhframework.core.FhFormException;

/**
 * NotSupportedFormException class. Thrown when form is not supported for desired operation.
 */
public class FhNotSupportedFormException extends FhFormException {

    /**
     * Constructs a new NotSupportedFormException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *                method.
     */
    public FhNotSupportedFormException(String message) {
        super(message);
    }

}
