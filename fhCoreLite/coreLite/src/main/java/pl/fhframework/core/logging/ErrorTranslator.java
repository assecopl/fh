package pl.fhframework.core.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.core.FhException;
import pl.fhframework.core.FhExternalServiceException;
import pl.fhframework.core.FhServiceConnectionException;
import pl.fhframework.core.FhStaleConversationException;
import pl.fhframework.core.i18n.MessageService;

import javax.persistence.OptimisticLockException;
import java.util.Optional;

/**
 * Created by pawel.ruta on 2018-03-02.
 */
@Service
public class ErrorTranslator {
    private static String OPTIMISTIC_LOCK = "fh.core.optimisticLock";
    private static String STALE_CONVERSATION = "fh.core.stale.conversation";
    private static String CONNECTION_EXCEPTION = "fh.core.connection.exception";
    private static String EXT_SERVICE_EXCEPTION = "fh.core.ext.service.exception";

    @Autowired
    MessageService messageService;

    public Optional<String> translateError(Throwable err) {
        if (isOptimisticLock(err)) {
            return Optional.of(messageService.getAllBundles().getMessage(OPTIMISTIC_LOCK));
        }
        if (isStaleConversation(err)) {
            return Optional.of(messageService.getAllBundles().getMessage(STALE_CONVERSATION));
        }
        if (isServiceConnectionException(err)) {
            return Optional.of(messageService.getAllBundles().getMessage(CONNECTION_EXCEPTION));
        }
        if (isRestClientException(err)) {
            return Optional.of(String.format(messageService.getAllBundles().getMessage(EXT_SERVICE_EXCEPTION), FhException.getRootCause(err).getMessage()));
        }
        return Optional.empty();
    }

    private boolean isOptimisticLock(Throwable err) {
        while (err != null) {
            if (err instanceof OptimisticLockException) {
                return true;
            }
            if (err.getCause() != err) {
                err = err.getCause();
            }
        }
        return false;
    }

    private boolean isStaleConversation(Throwable err) {
        while (err != null) {
            if (err instanceof FhStaleConversationException) {
                return true;
            }
            if (err.getCause() != err) {
                err = err.getCause();
            }
        }
        return false;
    }

    private boolean isRestClientException(Throwable err) {
        while (err != null) {
            if (err instanceof FhExternalServiceException) {
                return true;
            }
            if (err.getCause() != err) {
                err = err.getCause();
            }
        }
        return false;
    }

    private boolean isServiceConnectionException(Throwable err) {
        return err instanceof FhServiceConnectionException;
    }
}
