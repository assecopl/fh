package pl.fhframework.core.logging;

import pl.fhframework.Session;
import pl.fhframework.UserSession;
import pl.fhframework.model.dto.InMessageEventData;

/**
 * Created by pawel.ruta on 2019-02-11.
 */
public interface ISessionLogger {
    void logUserSessionCreation(Session newSession);

    void logSessionState(UserSession session);

    void logEvent(InMessageEventData eventData);
}
