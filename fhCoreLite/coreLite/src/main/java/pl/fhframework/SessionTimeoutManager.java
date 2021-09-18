package pl.fhframework;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.fhframework.core.session.ForceLogoutService;
import pl.fhframework.core.session.UserSessionRepository;
import pl.fhframework.event.dto.ForcedLogoutEvent;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(value = "fh.web.inactive_session_auto_logout")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SessionTimeoutManager {
    @Value("${fh.web.inactive_session_auto_logout:false}")
    private boolean active;

    @Value("${fh.web.inactive_session_max_time:10}")
    private int maxInactivityMinutes;

    @Value("${fh.web.inactive_session_counter_id:}")
    private String counterElementId;

    @Autowired
    private ForceLogoutService forceLogoutService;

    @Autowired
    private UserSessionRepository userSessionRepository;

    private Map<String, Instant> sessionActivityDictionary = new ConcurrentHashMap<>();

    public Session.TimeoutData keepSessionAlive(String conversationId) {
        if (!active) {
            throw new IllegalStateException("Session timeout management is not enabled.");
        }

        Instant activityLimitDate = sessionActivityDictionary.get(conversationId);
        if (activityLimitDate != null) {
            boolean allowedDuration = Instant.now().isBefore(activityLimitDate);
            if (allowedDuration) {
                activityLimitDate = getInstantMinutesLater();
                sessionActivityDictionary.put(conversationId, activityLimitDate);
            }
            userSessionRepository.onSessionKeepAlive(conversationId);
            return new Session.TimeoutData(activityLimitDate, counterElementId, maxInactivityMinutes);
        } else {
            throw new IllegalStateException("Cannot find session in activity dictionary. Make sure it has been properly initialized.");
        }
    }

    void registerConversation(String conversationId) {
        sessionActivityDictionary.put(conversationId, getInstantMinutesLater());
    }

    private Instant getInstantMinutesLater() {
        return Instant.now().plus(maxInactivityMinutes, ChronoUnit.MINUTES);
    }

    @Scheduled(fixedDelay = 500L)
    public synchronized void serverSideInactiveSessionsLogout() {
        if (active) {
            sessionActivityDictionary.keySet().removeIf(conversationId -> {
                Instant activityLimitDate = sessionActivityDictionary.get(conversationId);
                if (Instant.now().isBefore(activityLimitDate)) {
                    return false;
                } else {
                    forceLogoutService.forceLogout(conversationId, ForcedLogoutEvent.Reason.LOGOUT_TIMEOUT);
                    return true;
                }
            });
        }
    }
}
