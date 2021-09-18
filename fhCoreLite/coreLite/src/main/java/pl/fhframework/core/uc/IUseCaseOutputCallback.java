package pl.fhframework.core.uc;

import pl.fhframework.validation.IValidationResults;

import java.util.List;

public interface IUseCaseOutputCallback {
    default void exitOnValidation(IValidationResults validationResultsList) {

    }
}