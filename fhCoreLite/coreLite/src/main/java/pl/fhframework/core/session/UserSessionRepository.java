package pl.fhframework.core.session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pl.fhframework.UserSession;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.model.SessionInfo;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class UserSessionRepository implements HttpSessionListener, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(UserSessionRepository.class);
    //    private static final Logger log = LoggerFactory.getLogger(UserSessionRepository.class);
    @Getter
    private Map<String, UserSession> userSessions = new ConcurrentHashMap<>();
    @Getter
    private Map<String, HttpSession> orphanSessions = new ConcurrentHashMap<>();
    @Getter
    private Map<String, UserSession> userSessionsByConversationId = new ConcurrentHashMap<>();
    private Map<Integer, UserSession> userSessionsHash = new ConcurrentHashMap<>();
    private Set<Consumer<UserSession>> userSessionDestroyedListeners = new HashSet<>();
    private Set<Consumer<UserSession>> userSessionKeepAliveListeners = new HashSet<>();

    private final ApplicationContext applicationContext;
    private final SessionInfoCache sessionInfoCache;
    private SessionInfoService sessionInfoService;

    @Value("${server.port}")
    private int serverPort;
    @Value("${fh.session.info.protocol:http}")
    private String sessionInfoProtocol;

    /**
     * A flag that switch between spring scheduling and manual (by thread) implementation of cron.
     */
    @Value("${fh.session.force_clear_session_data_after_session_removal:false}")
    private boolean forceClearSessionDataAfterSessionRemoval;
    @Value("${fh.session.orphans.manage:false}")
    private boolean manageOrphans;

    private String nodeUrl;

    @PostConstruct
    public void init() {
        ((WebApplicationContext) applicationContext).getServletContext().addListener(this);
    }

    @Autowired
    public void setSessionInfoService(SessionInfoService sessionInfoService) {
        this.sessionInfoService = sessionInfoService;
    }

    @Autowired
    private LeakedSessionRemoverCron leakedSessionRemoverCron;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Override
    public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
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
        if(manageOrphans) {
            orphanSessions.remove(httpSessionId);
        }
        putSessionInfo(httpSessionId, userSession);
    }

    public boolean removeUserSession(String httpSessionId) {
        UserSession userSession = userSessions.remove(httpSessionId);
        if(userSession != null) {
            if(manageOrphans) {
                orphanSessions.put(httpSessionId, userSession.getHttpSession());
            }
            clearScopedBeans(userSession);
            UserSession removedHash = userSessionsHash.remove(System.identityHashCode(userSession.getHttpSession()));
            if(removedHash == null) {
                FhLogger.warn("User session {} not removed from userSessionsHash!  HTTPSessionId: {}", userSession, httpSessionId);
            }
            UserSession removedConversation = userSessionsByConversationId.remove(userSession.getConversationUniqueId());
            if(removedConversation == null) {
                FhLogger.warn("User session {} not removed from userSessionsByConversationId!  HTTPSessionId: {}", userSession, httpSessionId);
            }
            removeSessionInfo(httpSessionId);
            if (forceClearSessionDataAfterSessionRemoval) {
                userSession.removeAllValuesBeforeSessionRemove();
            }
            return true;
        } else {
            return false;
        }
    }

    private void clearScopedBeans(UserSession userSession) {
        userSession.getScopeBeanContainer().getDestructionCallbacks().clear();
        userSession.getScopeBeanContainer().getScopedObjects().clear();
        userSession.setScopeBeanContainer(null);
    }

    private synchronized void putSessionInfo(String httpSessionId, UserSession userSession) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setSessionId(userSession.getConversationUniqueId());
        sessionInfo.setHttpSessionId(httpSessionId);
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

    public UserSession getUserSession(HttpSession httpSession) {
        return userSessions.get(httpSession.getId());
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        // ChangeSessionIdAuthenticationStrategy silently modify session id after security filter, remove old userSession
        HttpSession session = httpSessionEvent.getSession();
        if (session.getAttribute(UserSession.FH_SESSION_ID) == null) {
            session.setAttribute(UserSession.FH_SESSION_ID, session.getId());
            long lastUsageMoment = System.currentTimeMillis();
            session.setAttribute("lastUsageTime", lastUsageMoment);
            session.setAttribute("lastUsageTimeStr", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        if(manageOrphans) {
            orphanSessions.put(session.getId(), session);
        }
        leakedSessionRemoverCron.startManualScheduler();
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        onSessionExpired(httpSessionEvent.getSession());
    }

    public void onSessionKeepAlive(String conversationId) {
        UserSession session = userSessionsByConversationId.get(conversationId);
        if (session != null) {
            for (Consumer<UserSession> listener : userSessionKeepAliveListeners) {
                listener.accept(session);
            }
        }
    }

    private void onSessionExpired(HttpSession httpSession) {
        UserSession session = getUserSession(httpSession);
        if (session != null) {
            try {
                for (Consumer<UserSession> listener : userSessionDestroyedListeners) {
                    listener.accept(session);
                }
            } finally {
                boolean response = removeUserSession(httpSession.getId());

                if (response) {
                    FhLogger.info("Removed expired session {} for {}. FhSessionId: {}, HTTPSessionID: {}",session, getUserLogin(session), session.getFhSessionId(), httpSession.getId());
                }else{
                    FhLogger.error("Unsuccessful attempt to delete the session for {}", getUserLogin(session));
                }
            }
        }
//        else {
//            FhLogger.info("Removed orphan HTTP session {}.", httpSession.getId());
//            orphanSessions.remove(httpSession);
//        }
        leakedSessionRemoverCron.cleanupLeakedSessions();
    }

    public void invalidateExpiredOrphanSessions(int emergencyRemovalTimeUnusedSessionInSeconds) {
        if(!manageOrphans) return;
        log.info("Invalidating expired orphan sessions not active for {} seconds...", emergencyRemovalTimeUnusedSessionInSeconds);
        for(String key : orphanSessions.keySet()) {
            HttpSession httpSession = orphanSessions.get(key);
            try {
                Long lastUsageTime = (Long) httpSession.getAttribute("lastUsageTime");
                if(lastUsageTime == null) {
                    FhLogger.warn("Orphan session {} has no last usage time", key);
                    continue;
                }
                String lastUsageTimeStr = (String) httpSession.getAttribute("lastUsageTimeStr");
                Long currentTime = System.currentTimeMillis();
                if(currentTime - lastUsageTime > emergencyRemovalTimeUnusedSessionInSeconds) {
                    FhLogger.info("Invalidating orphan HTTP session {}. Last used at {}.", key, lastUsageTimeStr);
//                    clearScopedBeans(httpSession);
                    SessionInformation si = sessionRegistry.getSessionInformation(httpSession.getId());
                    if (si != null) {
                        si.expireNow();
                    }
                    try {
                        httpSession.invalidate();
                    }  catch (Exception e) {
                        FhLogger.error("Error while invalidating orphan HTTP session {}", key, e);
                    }
                    orphanSessions.remove(key);
                } else {
                    FhLogger.info("Orphan HTTP session {} left active. Last used at {}.", key, lastUsageTimeStr);
                }
            } catch (IllegalStateException e) {
                if(e.getMessage().contains("Session is invalid")) {
                    FhLogger.error("Session {} is invalid. Removing from orphans", key);
                    orphanSessions.remove(key);
                }
            }
            catch (Exception ex) {
                FhLogger.error("Exception caught during orphans invalidation for session {}:\n{}", httpSession.getId(), ExceptionUtils.getStackTrace(ex));
            }
        }
    }

    public Set<UserSession> getAllUserSessions(){
        return new HashSet<>(userSessions.values());
    }

    protected Map<String, UserSession> getUserSessionsByFhId(){
        return Collections.unmodifiableMap(this.userSessions);
    }


    protected UserSession getUserSessionByFhId(String fhId) {
        return this.userSessions.get(fhId);
    }

    protected int getNoOfSessions(){
        return userSessions.size();
    }

    public static String getUserLogin(UserSession userSession){
        try{
            return userSession.getSystemUser().getLogin();
        }catch (Exception ex){
            return "unkwnon user";
        }
    }

    public UserSession getUserSession(String httpSessionId) {
        return userSessions.get(httpSessionId);
    }

    public void removeUserSession(HttpSession httpSession){
        onSessionExpired(httpSession);
    }

}
