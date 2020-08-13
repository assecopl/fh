package pl.fhframework.core.session;


import pl.fhframework.ISessionManagerImpl;
import pl.fhframework.Session;

/**
 * Manager of user context in terms of interrelated web socket session , http session and user session
 */
public class FhSessionManager implements ISessionManagerImpl {

    /**
     * Fh session
     */
    private Session session;


    public Session getSession() {
        return session;
    }

    public FhSessionManager(Session session) {
        this.session = session;
    }
}
