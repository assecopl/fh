package pl.fhframework;


import pl.fhframework.model.security.SystemUser;

/**
 * Manager of user context and session.
 */
public abstract class SessionManager {

    private static final ThreadLocal<ISessionManagerImpl> THREAD_SESSION_MANAGER = new ThreadLocal<>();

    public static Session getSession() {
        ISessionManagerImpl instance = THREAD_SESSION_MANAGER.get();
        return instance != null ? instance.getSession() : null;
    }

    public static NoUserSession getNoUserSession() {
        ISessionManagerImpl instance = THREAD_SESSION_MANAGER.get();
        return instance != null && instance.getSession() instanceof NoUserSession ? (NoUserSession) instance.getSession() : null;
    }

    public static UserSession getUserSession() {
        ISessionManagerImpl instance = THREAD_SESSION_MANAGER.get();
        return instance != null && instance.getSession() instanceof UserSession ? (UserSession) instance.getSession() : null;
    }

    public static SystemUser getSystemUser() {
        Session session = getSession();
        return session != null ? session.getSystemUser() : null;
    }

    public static String getUserLogin() {
        SystemUser user = getSystemUser();
        return user != null ? user.getLogin() : null;
    }

    public static void registerThreadSessionManager(ISessionManagerImpl sessionManager) {
        THREAD_SESSION_MANAGER.set(sessionManager);
    }

    public static void unregisterThreadSessionManager() {
        THREAD_SESSION_MANAGER.remove();
    }
}
