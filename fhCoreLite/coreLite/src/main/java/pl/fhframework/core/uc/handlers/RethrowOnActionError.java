package pl.fhframework.core.uc.handlers;

import org.hibernate.StaleStateException;
import org.springframework.stereotype.Component;
import pl.fhframework.core.FhException;
import pl.fhframework.core.FhStaleConversationException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.UseCaseContainer;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

/**
 * Created by pawel.ruta on 2018-11-26.
 */
@Component
public class RethrowOnActionError implements IOnActionErrorHandler {
    @Override
    public void onStartError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext) {
        // try to clean up
        try {
            useCaseContainer.terminateUseCase(useCaseContext, true);
        } catch (Throwable error2) {
            FhLogger.errorSuppressed("Exception while force cleaning after UC's start() failure", error2);
        }

        // rethrow
        throw error;
    }

    @Override
    public void onActionError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, RuntimeException error, boolean invalidPersistenceContext) {
        Throwable rootException = FhException.getRootCause(error);
        if (rootException instanceof PersistenceException &&
                !(rootException instanceof StaleStateException) &&
                !(rootException instanceof OptimisticLockException) &&
                invalidPersistenceContext) {
            throw new FhStaleConversationException(error);
        }
        throw error;
    }
}
