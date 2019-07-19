package pl.fhframework.validation;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.PresentationStyleEnum;

@Getter
@Setter
public class FieldValidationResult {
    private PresentationStyleEnum presentationStyleEnum;
    private String message;
    private String knownSourceComponentId;
    private boolean formSource;

    public FieldValidationResult() {
    }

    public FieldValidationResult(PresentationStyleEnum presentationStyleEnum, String message) {
        this.presentationStyleEnum = presentationStyleEnum;
        this.message = message;
    }

    public FieldValidationResult(PresentationStyleEnum presentationStyleEnum, String message, String knownSourceComponentId) {
        this.presentationStyleEnum = presentationStyleEnum;
        this.message = message;
        this.knownSourceComponentId = knownSourceComponentId;
    }
}
