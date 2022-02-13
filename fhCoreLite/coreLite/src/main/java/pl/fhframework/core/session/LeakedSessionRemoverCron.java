package pl.fhframework.core.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.fhframework.UserSession;
import pl.fhframework.core.logging.FhLogger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Mechanism for temporaly session deletion. Contains cron which seek for abandoned session and removes it.
 */
@Component
public class LeakedSessionRemoverCron {
    @Autowired
    private UserSessionRepository userSessionRepository;

    /**
     * A flag that turns on the mechanism for emergency session deletion. Default on/true value.
     */
    @Value("${fh.session.emergency_removal_unused_sessions:true}")
    private boolean emergencyRemovalUnusedSessions;

    /**
     * The maximum amount of time an unused session can survive - devault value = 12 houres (43200 seconds)
     */
    @Value("${fh.session.emergency_removal_time_unused_session:43200}")
    private int emergencyRemovalTimeUnusedSessionInSeconds;

    /**
     * The period for scheduler - devault value = 1 houre (3600 seconds)
     */
    @Value("${fh.session.leaked_session_remover_cron_period:3600}")
    private int leakedSessionRemoverPeriod;

    @Value("${dupa:Ala}")
    private String ddd;

    /**
     * Time in millis when the last time cron has been working
     */
    private long lastCronTime = 0;

    /**
     * Cron seeks leaked sessions in period defined in fh.session.leaked_session_remover_cron_period (leakedSessionRemoverPeriod)
     */
    @Scheduled(fixedDelay = 1000)
    private void cleanupLeakedSessions(){
        if (emergencyRemovalUnusedSessions && lastCronTime<=System.currentTimeMillis() - leakedSessionRemoverPeriod * 1000) {
            lastCronTime = System.currentTimeMillis();
            FhLogger.info("Cleanup leaked sessions cron: Seeking for outdated sessions. There are {} sessions. The oldest one hasn't been used since {} seconds.", userSessionRepository.getNoOfSessions(), getInactivityTimeForMostExpiredSessions()/1000);
            Set<String> sessionKeysToRemove = getKeysToRemove(userSessionRepository.getUserSessionsByFhId().entrySet());
            if (sessionKeysToRemove.size()>0){
                //Removing sessions in convinient way
                emergencySessionRemoval(sessionKeysToRemove);
                FhLogger.warn("Cleanup leaked sessions cron: removed {} sessions. Now the oldest one hasn't been used since {} seconds.", sessionKeysToRemove.size(), getInactivityTimeForMostExpiredSessions() / 1000);
            }

            //Calculate leaked sessions in userSessionsByConversationId
            sessionKeysToRemove = getKeysToRemove(userSessionRepository.getUserSessionsByConversationId().entrySet());
            if (sessionKeysToRemove.size()>0) {
                unkomonEmergencySessionRemovalInUserSessionsByConversationId(sessionKeysToRemove);
            }
        }
    }

    /**
     * Returns inactivity time for most expired session
     * @return Value in millis
     */
    private long getInactivityTimeForMostExpiredSessions(){
        UserSession mostExpiredOne = getMostExpiredSession();
        if (mostExpiredOne!=null){
            return mostExpiredOne.getHowLongIsUnusedInMillis();
        }else{
            return 0;
        }
    }

    /**
     * Return most expired session or null if there are no sessions
     * @return Session which has the longest unused time
     */
    private UserSession getMostExpiredSession(){
        UserSession oldestUsedSession = null;
        for (UserSession i : userSessionRepository.getAllUserSessions()){
            if (oldestUsedSession == null || oldestUsedSession.getLastUsageMoment() > i.getLastUsageMoment()){
                oldestUsedSession = i;
            }
        }
        return oldestUsedSession;
    }

    /**
     * Removes session given in param
     * @param sessionKeysToRemove FhId keys for sessions which must be removed
     */
    private void emergencySessionRemoval(Set<String> sessionKeysToRemove) {
        sessionKeysToRemove.forEach(key -> {
            UserSession session = userSessionRepository.getUserSessionByFhId(key);
            FhLogger.warn(UserSessionRepository.class, "Emergency removal of unnecessary session {}", key, UserSessionRepository.getUserLogin(session) );
            userSessionRepository.removeUserSession(key);
        });
    }

    /**
     * Emergency for emergency session removal;) This methcod should be unnecessary but shit happens ;)
     * @param sessionKeysToRemove Set of confersation id keys - should be empty but shit happens
     */
    private void unkomonEmergencySessionRemovalInUserSessionsByConversationId(Set<String> sessionKeysToRemove) {
        if (sessionKeysToRemove.size()>0){
            FhLogger.error("Lost sessions in userSessionsByConversationId!!!");
            sessionKeysToRemove.forEach(key -> {
                UserSession session = userSessionRepository.getUserSessionsByConversationId().get(key);
                FhLogger.error("Removing {} in userSessionsByConversationId for user {}", key, UserSessionRepository.getUserLogin(session));
                userSessionRepository.getUserSessionsByConversationId().remove(key);
            });
        }
    }

    /**
     * Returns set of sessions which shoutd removed
     * @param entries Entry set of all sessions.
     * @param <T> Type of keys
     * @return set of session keys to remove
     */
    private <T> Set<T> getKeysToRemove(Set<Map.Entry<T, UserSession>> entries){
        Set<T> sessionKeysToRemove = new HashSet<>();
        entries.forEach(entry -> {
            UserSession session = entry.getValue();
            if (doesSessionShouldBeRemoved(session)){
                sessionKeysToRemove.add(entry.getKey());
            }
        });
        return sessionKeysToRemove;
    }


    /**
     * Rule that determines whether the given session should be removed
     * @param userSession Checked session
     * @return True if session shoud be removed
     */
    private boolean doesSessionShouldBeRemoved(UserSession userSession) {
        return userSession.hasNotBeenUsedIn(emergencyRemovalTimeUnusedSessionInSeconds *1000);
    }


}
