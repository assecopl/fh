package pl.fhframework.compiler.forms;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Component;

/**
 * Result of form file validation
 */
@Getter
@Setter
public class CompilationValidationResult {

    public enum Status {
        SUCCESS,
        GENERATION_ERROR,
        COMPILATION_ERROR
    }

    public CompilationValidationResult(Status status, String componentId, String message, String stackTrace) {
        this.status = status;
        this.componentId = componentId;
        this.message = message;
        this.componentDisplayName = componentId;
        this.stackTrace = stackTrace;
    }

    private Status status;

    @Getter
    @Setter
    private String componentId;

    @Getter
    @Setter
    private Component component;

    @Getter
    @Setter
    private String componentDisplayName;

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private String stackTrace;

    public boolean isGenerationFailure() {
        return status == Status.GENERATION_ERROR;
    }

    public boolean isCompilationFailure() {
        return status == Status.COMPILATION_ERROR;
    }

    public boolean isSuccessful() {
        return status == Status.SUCCESS;
    }
}
