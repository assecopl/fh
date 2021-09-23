package pl.fhframework.core.session;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.model.SessionInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User sessions info service proxy component
 * @author Tomasz Kozlowski (created on 09.01.2020)
 */
@Service
public class SessionInfoService {

    @Setter
    private IUserSessionService userSessionService;
    private SessionInfoAPIClient sessionInfoAPIClient;

    @Value("${fhframework.managementApi.enabled:false}")
    private boolean managementApiEnabled;

    @Autowired
    public SessionInfoService(IUserSessionService userSessionService, SessionInfoAPIClient sessionInfoAPIClient) {
        this.userSessionService = userSessionService;
        this.sessionInfoAPIClient = sessionInfoAPIClient;
    }

    /** Returns lists of all user sessions info */
    public List<SessionInfo> getUserSessions() {
        List<SessionInfo> sessions = userSessionService.getUserSessions();
        // set lazy read of user active functionality
        sessions.forEach(
            session -> session.setActiveUseCaseFunction(
                    this::getUserActiveFunctionality
            )
        );
        return sessions;
    }

    /** Gets info about active functionality for given user session */
    public String getUserActiveFunctionality(SessionInfo sessionInfo) {
        if (managementApiEnabled) {
            return sessionInfoAPIClient.getUserActiveFunctionality(sessionInfo);
        } else {
            return userSessionService.getUserActiveFunctionality(sessionInfo.getSessionId());
        }
    }

    /** Displays given message to specified users */
    public int sendMessage(String nodeUrl, List<SessionInfo> sessions, String title, String message) {
        if (managementApiEnabled) {
            return sessionInfoAPIClient.sendMessage(nodeUrl, sessions, title, message);
        } else {
            List<String> sessionIds = sessions.stream()
                    .map(SessionInfo::getSessionId)
                    .collect(Collectors.toList());
            return userSessionService.sendMessage(sessionIds, title, message);
        }
    }

    /** Force logout of given user */
    public boolean forceLogout(SessionInfo sessionInfo) {
        if (managementApiEnabled) {
            return sessionInfoAPIClient.forceLogout(sessionInfo);
        } else {
            return userSessionService.forceLogout(sessionInfo.getSessionId());
        }
    }

    /** Downloads given user log file */
    public Resource downloadUserLog(SessionInfo sessionInfo) {
        if (managementApiEnabled) {
            return sessionInfoAPIClient.donwloadUserLog(sessionInfo);
        } else {
            return userSessionService.donwloadUserLog(sessionInfo.getSessionId());
        }
    }

    /** Returns whether given node is active */
    public boolean isNodeActive(String nodeUrl) {
        if (managementApiEnabled) {
            return sessionInfoAPIClient.isNodeActive(nodeUrl);
        } else {
            return true; // only local user sessions
        }
    }

}
