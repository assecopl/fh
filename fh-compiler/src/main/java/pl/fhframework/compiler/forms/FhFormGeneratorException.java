package pl.fhframework.compiler.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.FhFormException;

/**
 * Exception thrown on form generation error
 */
public class FhFormGeneratorException extends FhFormException {

    @Getter
    @Setter
    private String faultyComponentId;

    public FhFormGeneratorException(String faultyComponentId, String message) {
        super(message);
        this.faultyComponentId = faultyComponentId;
    }

    public FhFormGeneratorException(String faultyComponentId, String message, Throwable cause) {
        super(message, cause);
        this.faultyComponentId = faultyComponentId;
    }
}
