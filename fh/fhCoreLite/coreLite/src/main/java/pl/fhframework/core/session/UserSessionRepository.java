package pl.fhframework.core.session;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pl.fhframework.UserSession;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
public class UserSessionRepository implements HttpSessionListener {

    @Getter
    private Map<String, UserSession> userSessions = new ConcurrentHashMap<>();
    private Map<String, UserSession> userSessionsByConversationId = new ConcurrentHashMap<>();
    private Map<Integer, UserSession> userSessionsHash = new ConcurrentHashMap<>();
    private Set<Consumer<UserSession>> userSessionDestroyedListeners = new HashSet<>();
    private Set<Consumer<UserSession>> userSessionKeepAliveListeners = new HashSet<>();

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        ((WebApplicationContext) applicationContext).getServletContext().addListener(this);
    }

    public int getUserSessionCount() {
        return userSessions.size();
    }

    public void addUserSessionDestroyedListener(Consumer<UserSession> listener) {
        userSessionDestroyedListeners.add(listener);
    }

    public void addUserSessionKeepAliveListener(Consumer<UserSession> listener) {
        userSessionKeepAliveListeners.add(listener);
    }

    public void setUserSession(String httpSessionId, UserSession userSession) {
        // ChangeSessionIdAuthenticationStrategy silently modify session id after security filter, remove old userSession
        int httpSessionHash = System.identityHashCode(userSession.getHttpSession());
        UserSession oldUserSession = userSessionsHash.get(httpSessionHash);
        if (oldUserSession != null) {
            removeUserSession(oldUserSession.getHttpSessionOrgId());
        }
        userSessionsHash.put(httpSessionHash, userSession);
        userSessions.put(httpSessionId, userSession);
        userSessionsByConversationId.put(userSession.getConversationUniqueId(), userSession);
    }

    public void removeUserSession(String httpSessionId) {
        UserSession userSession = userSessions.remove(httpSessionId);
        userSessionsHash.remove(System.identityHashCode(userSession.getHttpSession()));
        userSessionsByConversationId.remove(userSession.getConversationUniqueId());
    }

    public UserSession getUserSession(String httpSessionId) {
        return userSessions.get(httpSessionId);
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        // ignore
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        onSessionExpired(httpSessionEvent.getSession().getId());
    }

    public void onSessionKeepAlive(String conversationId) {
        UserSession session = userSessionsByConversationId.get(conversationId);
        if (session != null) {
            for (Consumer<UserSession> listener : userSessionKeepAliveListeners) {
                listener.accept(session);
            }
        }
    }

    private void onSessionExpired(String sessionId) {
        UserSession session = userSessions.get(sessionId);
        if (session != null) {
            try {
                for (Consumer<UserSession> listener : userSessionDestroyedListeners) {
                    listener.accept(session);
                }
            } finally {
                UserSession userSession = userSessions.remove(sessionId);
                int httpSessionHash = System.identityHashCode(userSession.getHttpSession());
                userSessionsHash.remove(httpSessionHash);
                userSessionsByConversationId.remove(userSession.getConversationUniqueId());
            }
        }
    }

}
