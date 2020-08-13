package pl.fhframework.core;

/**
 * Created by pawel.ruta on 2018-08-31.
 */
public abstract class FhMessageException extends FhException {
    public FhMessageException() {
    }

    public FhMessageException(String message) {
        super(message);
    }

    public FhMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public FhMessageException(Throwable cause) {
        super(cause);
    }
}
