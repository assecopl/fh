package pl.fhframework.core.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pl.fhframework.UserSession;
import pl.fhframework.WebSocketSessionManager;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.model.SessionInfo;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class UserSessionRepository implements HttpSessionListener, ApplicationListener<ContextRefreshedEvent> {

    @Getter
    private Map<String, UserSession> userSessions = new ConcurrentHashMap<>();
    private Map<String, UserSession> userSessionsByConversationId = new ConcurrentHashMap<>();
    private Map<Integer, UserSession> userSessionsHash = new ConcurrentHashMap<>();
    private Set<Consumer<UserSession>> userSessionDestroyedListeners = new HashSet<>();
    private Set<Consumer<UserSession>> userSessionKeepAliveListeners = new HashSet<>();

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private final SessionInfoCache sessionInfoCache;
    @Autowired
    private SessionInfoService sessionInfoService;

    @Value("${server.port}")
    private int serverPort;
    @Value("${fh.session.info.protocol:http}")
    private String sessionInfoProtocol;
    @Value("${fh.ws.closed.inactive_session_max_time:5}")
    private int sustainTimeOutMinutes;

    private String nodeUrl;



    @PostConstruct
    public void init() {
        ((WebApplicationContext) applicationContext).getServletContext().addListener(this);
    }

    @Autowired
    public void setSessionInfoService(SessionInfoService sessionInfoService) {
        this.sessionInfoService = sessionInfoService;
    }

    @Override
    public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
        WebSocketSessionManager.setSustainTimeout(sustainTimeOutMinutes * 60);

        // register node in cache
        nodeUrl = generateNodeUrl();
        int iter = 0;
        do {
            iter++;
            Set<String> nodes = sessionInfoCache.getNodes();
            nodes.add(nodeUrl);
            sessionInfoCache.putNodes(nodes);

            // wait a while and check whether node has been added, if not then try again
            try {
                Thread.sleep(new Random().nextInt(300) + 200);
            } catch (InterruptedException e) {
                // nothing
            }
        } while (!sessionInfoCache.getNodes().contains(nodeUrl) && iter < 5);

        // set empty collection for user sessions info
        sessionInfoCache.putSessionsInfoForNode(nodeUrl, new ConcurrentHashMap<>());
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
        putSessionInfo(httpSessionId, userSession);
    }

    public void removeUserSession(String httpSessionId) {
        UserSession userSession = userSessions.remove(httpSessionId);
        userSessionsHash.remove(System.identityHashCode(userSession.getHttpSession()));
        userSessionsByConversationId.remove(userSession.getConversationUniqueId());
        removeSessionInfo(httpSessionId);
    }

    private synchronized void putSessionInfo(String httpSessionId, UserSession userSession) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setSessionId(userSession.getConversationUniqueId());
        sessionInfo.setLogonTime(new Date(userSession.getCreationTimestamp().toEpochMilli()));
        sessionInfo.setUserName(userSession.getSystemUser().getLogin());
        sessionInfo.setNodeUrl(nodeUrl);
        // put into cache
        Map<String, SessionInfo> sessionsInfo = sessionInfoCache.getSessionsInfoForNode(nodeUrl);
        sessionsInfo.put(httpSessionId, sessionInfo);
        sessionInfoCache.putSessionsInfoForNode(nodeUrl, sessionsInfo);
    }

    private synchronized void removeSessionInfo(String httpSessionId) {
        Map<String, SessionInfo> sessionsInfo = sessionInfoCache.getSessionsInfoForNode(nodeUrl);
        sessionsInfo.remove(httpSessionId);
        sessionInfoCache.putSessionsInfoForNode(nodeUrl, sessionsInfo);
    }

    private String generateNodeUrl() {
        try {
            return String.format(
                "%s://%s:%s/",
                sessionInfoProtocol,
                InetAddress.getLocalHost().getHostAddress(),
                serverPort
            );
        } catch (UnknownHostException e) {
            FhLogger.errorSuppressed(e);
            return null;
        }
    }

    public Map<String, SessionInfo> getAllUserSessionsInfo() {
        Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();
        Set<String> nodes = sessionInfoCache.getNodes();
        for (String node : nodes) {
            if (sessionInfoService.isNodeActive(node)) {
                sessions.putAll(sessionInfoCache.getSessionsInfoForNode(node));
            } /*else {
                removeNodeFromCache(node);
            }*/
        }
        return sessions;
    }

    private synchronized void removeNodeFromCache(String node) {
        Set<String> nodes = sessionInfoCache.getNodes();
        nodes.remove(node);
        sessionInfoCache.putNodes(nodes);
        sessionInfoCache.evictSessionsInfoForNode(node);
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
                removeUserSession(sessionId);
            }
        }
    }

}
