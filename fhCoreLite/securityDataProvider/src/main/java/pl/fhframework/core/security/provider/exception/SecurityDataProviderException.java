package pl.fhframework.core.security.provider.exception;

/**
 * Common exception class for Data Security Provider.
 * @author tomasz.kozlowski (created on 2017-12-07)
 */
public class SecurityDataProviderException extends RuntimeException {

    public SecurityDataProviderException(String message) {
        super(message);
    }

    public SecurityDataProviderException(String message, Throwable cause) {
        super(message, cause);
    }

}
