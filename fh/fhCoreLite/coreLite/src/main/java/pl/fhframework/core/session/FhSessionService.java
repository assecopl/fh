package pl.fhframework.core.session;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import pl.fhframework.aop.services.IFhSessionService;
import pl.fhframework.aop.services.IFhTransactionListener;
import pl.fhframework.core.FhConversationException;
import pl.fhframework.core.logging.LogLevel;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.SecurityManager;
import pl.fhframework.core.util.JacksonUtils;
import pl.fhframework.Session;
import pl.fhframework.SessionDescription;
import pl.fhframework.SessionManager;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manager of user context in terms of interrelated web socket session , http session and user session
 */
@Service
public class FhSessionService implements IFhSessionService {
    /**
     * Timeout of http session when WS session is interrupted
     * This time should allow user to reconnect (to the same server or other node in cluster)
     */
    public static final int SUSTAIN_TIMEOUT = 5 * 60; // minutes

    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Autowired
    private SecurityManager securityManager;

    @Autowired(required = false)
    private IFhTransactionListener transactionListener;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JacksonUtils jacksonUtils;

    @Override
    public boolean onServiceStart(Method operation, Object owner, Object[] params) {
        if (SessionManager.getSession() == null) {
            SessionDescription description = new SessionDescription();
            description.setConversationUniqueId(UUID.randomUUID().toString());
            Session session = (Session) applicationContext.getBean("noUserSession", description);
            FhLogger.log(LogLevel.DEBUG, "Started no user context session id '{}'", description.getConversationUniqueId());

            sessions.put(description.getConversationUniqueId(), session);

            SessionManager.registerThreadSessionManager(new FhSessionManager(session));

            try {
                manageTransactionOnStart(operation, owner, params);

                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    session.setSystemUser(securityManager.buildSystemUser(SecurityContextHolder.getContext().getAuthentication()));
                }

                return true;
            }
            catch (Exception e) {
                sessions.remove(description.getConversationUniqueId());
                SessionManager.unregisterThreadSessionManager();
            }
        }
        if (isNoUserSession()) {
            manageTransactionOnStart(operation, owner, params);
        }

        return false;
    }

    @Override
    public void onServiceEnd(Method operation, Object owner, Object[] retValHolder, boolean endSession) {
        if (isNoUserSession()) {
            try {
                if (endSession) {
                    manageHttpRequest(retValHolder[0]);
                }

                manageTransactionOnEnd(operation, owner, retValHolder);
            }
            finally {
                if (endSession) {
                    clearSession();
                }
            }
        }
    }

    @Override
    public void onError(Method method, Object owner, Exception e, boolean endSession) {
        if (isNoUserSession()) {
            try {
                FhLogger.error(e);
                transactionListener.onError(getTransactionPropagation(method, owner), owner);
            }
            finally {
                if (endSession) {
                    clearSession();
                }
            }
        }
        else {
            if (getTransactionPropagation(method, owner) != null) {
                transactionListener.invalidate();
            }
        }
    }

    @Scheduled(fixedDelay = 60000)
    protected void clearNotActiveSessions() {
        // todo: update session timestamp
        List<String> expiredKeys = sessions.entrySet().stream().filter(sessionEntry -> Instant.now().minus(SUSTAIN_TIMEOUT, ChronoUnit.MINUTES).compareTo(sessionEntry.getValue().getCreationTimestamp()) > 0).map(Map.Entry::getKey).collect(Collectors.toList());

        for (String sessionId : expiredKeys) {
            Session session = sessions.remove(sessionId);
            FhLogger.log(LogLevel.WARN, "Terminating no user context session id '{}' due to time out", session.getDescription().getConversationUniqueId());
        }
    }

    private void manageTransactionOnStart(Method operation, Object owner, Object[] params) {
        Integer transactionPropagation = getTransactionPropagation(operation, owner);
        if (transactionPropagation != null && transactionListener == null) {
            throw new FhConversationException("No conversation manager for stateless services");
        }

        if (transactionListener != null) {
            transactionListener.onStart(transactionPropagation, owner, params);
        }
    }

    private void manageTransactionOnEnd(Method operation, Object owner, Object[] outputParams) {
        if (transactionListener != null) {
            transactionListener.onEnd(getTransactionPropagation(operation, owner), owner, outputParams);
        }
    }

    private void clearSession() {
        Session session = SessionManager.getNoUserSession();
        SessionManager.unregisterThreadSessionManager();
        FhLogger.log(LogLevel.DEBUG, "Stopped no user context session id '{}'", session.getDescription().getConversationUniqueId());
        sessions.remove(session.getDescription().getConversationUniqueId());
        FhLogger.log(LogLevel.DEBUG, "Released no user context session id '{}'", session.getDescription().getConversationUniqueId());
    }

    private boolean isNoUserSession() {
        return SessionManager.getSession() == null || !SessionManager.getSession().isUserContext();
    }

    private Integer getTransactionPropagation(Method operation, Object owner) {
        Transactional transactional = operation.getAnnotation(Transactional.class);
        if (transactional == null) {
            transactional = owner.getClass().getAnnotation(Transactional.class);
        }
        if (transactional != null) {
            return transactional.propagation().value();
        }

        return null;
    }

    private void manageHttpRequest(Object value) {
        if (JacksonUtils.isHttpRequest()) {
            RequestContextHolder.getRequestAttributes().setAttribute(JacksonUtils.ATTR_NAME, jacksonUtils.valueToTree(value), RequestAttributes.SCOPE_REQUEST);
        }
    }
}
