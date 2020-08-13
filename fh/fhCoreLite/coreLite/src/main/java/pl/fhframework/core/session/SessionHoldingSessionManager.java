package pl.fhframework.core.session;

import pl.fhframework.ISessionManagerImpl;
import pl.fhframework.UserSession;

/**
 * Session manager implementation that holds UserSession object.
 */
public class SessionHoldingSessionManager implements ISessionManagerImpl {

    private UserSession userSession;

    public SessionHoldingSessionManager(UserSession userSession) {
        this.userSession = userSession;
    }

    @Override
    public UserSession getSession() {
        return userSession;
    }
}
