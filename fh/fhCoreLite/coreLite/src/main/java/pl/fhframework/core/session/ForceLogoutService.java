package pl.fhframework.core.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.UserSession;
import pl.fhframework.WebSocketContext;
import pl.fhframework.WebSocketSessionRepository;
import pl.fhframework.event.dto.ForcedLogoutEvent;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service used to logout user
 */
@Service
public class ForceLogoutService {

    @Autowired
    private WebSocketSessionRepository webSocketSessionRepository;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserSessionRepository userSessionRepository;
    
    public boolean forceLogoutByUsername(String username, ForcedLogoutEvent.Reason reason) {
        for (UserSession session : findUserSessionsByUsername(username)) {
            forceLogout(session, reason);
        }
        return true;
    }

    public boolean forceLogout(String sessionConversationUniqueId, ForcedLogoutEvent.Reason reason) {
        return forceLogout(findUserSessionByConversationId(sessionConversationUniqueId), reason);
    }

    public boolean forceLogout(UserSession userSession, ForcedLogoutEvent.Reason reason) {
        if (userSession == null) {
            return false;
        }
        try {
            HttpSession httpSession = userSession.getHttpSession();
            String httpSessionId = httpSession.getId();
            try {
                userSession.clearUseCaseStack();
            } catch (Exception e) {
                FhLogger.error(e); // ignore
            }

            // push info to client
            Optional<WebSocketSession> wsSession = webSocketSessionRepository.getSession(userSession);
            if (wsSession.isPresent() && wsSession.get().isOpen()) {
                try {
                    userSession.pushForcedLogoutInfo(WebSocketContext.from(userSession, wsSession.get()), reason);
                    webSocketSessionRepository.closeSession(wsSession.get());
                } catch (Exception e) {
                    FhLogger.error(e); // ignore - user will be logout on server side and will have to authenticate again
                }
            }

            SessionInformation si = sessionRegistry.getSessionInformation(httpSessionId);
            if (si != null) {
                si.expireNow();
            }
            httpSession.invalidate();
            return true;
        } catch (Exception e) {
            FhLogger.error(e);
            return false;
        }
    }

    private UserSession findUserSessionByConversationId(String sessionConversationUniqueId) {
        for (UserSession userSession : userSessionRepository.getUserSessions().values()) {
            if (userSession.getConversationUniqueId().equals(sessionConversationUniqueId)) {
                return userSession;
            }
        }
        return null;
    }

    private Collection<UserSession> findUserSessionsByUsername(String username) {
        return userSessionRepository.getUserSessions().values().stream()
                .filter(s -> s.getSystemUser().getLogin().equals(username))
                .collect(Collectors.toList());
    }


}
