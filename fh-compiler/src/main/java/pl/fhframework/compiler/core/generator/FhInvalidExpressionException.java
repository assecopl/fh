package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import pl.fhframework.core.FhException;

/**
 * Exception throw during type recognision when an unsupported (but valid) expression found.
 */
@Getter
public class FhInvalidExpressionException extends FhException {

    public FhInvalidExpressionException(String message) {
        super(message);
    }
}
