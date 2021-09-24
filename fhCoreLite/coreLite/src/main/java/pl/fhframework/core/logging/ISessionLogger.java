package pl.fhframework.core.logging;

import pl.fhframework.Session;
import pl.fhframework.UserSession;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.model.dto.AbstractMessage;
import pl.fhframework.model.dto.InMessageEventData;

/**
 * Created by pawel.ruta on 2019-02-11.
 */
public interface ISessionLogger {
    void logUserSessionCreation(Session newSession);

    void logSessionState(UserSession session);

    void logSessionEndState(UserSession session);

    void logEvent(InMessageEventData eventData);

    void logShowForm(Class<?> formClazz, Object model, String variantId);

    /**
     * Method allows tracing of incoming messages from client and its running time (includes deserialization of request,
     * event processing, serialization of response)
     *
     * @param requestId  request id
     * @param inMessage  message from client
     * @param topUseCase current (before event processing) top usecase on stack, can be null if no usecase
     * @param exception  possible exception that occurred during event processing
     * @param time       time in nanoseconds
     */
    default void logReqestResponse(String requestId, AbstractMessage inMessage, IUseCase topUseCase, Throwable exception, Long time) {}
}
