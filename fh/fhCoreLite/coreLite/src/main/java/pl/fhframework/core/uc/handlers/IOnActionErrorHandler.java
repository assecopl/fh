package pl.fhframework.core.uc.handlers;

import pl.fhframework.core.uc.FormsContainer;
import pl.fhframework.core.uc.UseCaseContainer;

/**
 * Created by pawel.ruta on 2018-11-23.
 */
public interface IOnActionErrorHandler {
    String ACTION_ERROR_MESSAGE = "fh.core.action.error";

    void onStartError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext);

    void onActionError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext);

    default String buildMessage(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext) {
        return error != null ? error.getMessage() : null;
    }

    default String messageTitle() {
        return null;
    }
}
