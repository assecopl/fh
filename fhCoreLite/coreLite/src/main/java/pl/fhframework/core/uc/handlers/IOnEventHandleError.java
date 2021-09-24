package pl.fhframework.core.uc.handlers;

import pl.fhframework.FormsHandler;
import pl.fhframework.core.uc.UseCaseContainer;

public interface IOnEventHandleError {
    void onEventHandleError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, Throwable error, FormsHandler formsHandler, String requestId);
}
