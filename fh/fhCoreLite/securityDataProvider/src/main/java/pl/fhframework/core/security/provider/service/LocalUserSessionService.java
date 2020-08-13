package pl.fhframework.core.security.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import pl.fhframework.UserSession;
import pl.fhframework.WebSocketContext;
import pl.fhframework.WebSocketSessionRepository;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.model.SessionInfo;
import pl.fhframework.core.session.ForceLogoutService;
import pl.fhframework.core.session.IUserSessionService;
import pl.fhframework.core.session.UserSessionRepository;
import pl.fhframework.core.util.LogUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.dto.ForcedLogoutEvent;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User session service implementation for local sessions.
 */
@Service
public class LocalUserSessionService implements IUserSessionService {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private WebSocketSessionRepository webSocketSessionRepository;

    @Autowired
    private ForceLogoutService forceLogoutService;

    @Override
    public List<SessionInfo> getUserSessions() {
        return new ArrayList<>(userSessionRepository.getAllUserSessionsInfo().values());
    }

    @Override
    public boolean forceLogout(String userSessionConversationId) {
        return forceLogoutService.forceLogout(userSessionConversationId, ForcedLogoutEvent.Reason.LOGOUT_FORCE);
    }

    @Override
    public Resource donwloadUserLog(String userSessionConversationId) {
        UserSession userSession = findUserSession(userSessionConversationId);
        if (userSession != null) {
            URL log = LogUtils.getUserLogFile(userSession);
            return new UrlResource(log);
        } else {
            return null;
        }
    }

    @Override
    public int sendMessage(List<String> userSessionConversationIds, String title, String message) {
        List<UserSession> userSessions = findUserSessions(new HashSet<>(userSessionConversationIds));
        int successCount = 0;
        for (UserSession userSession : userSessions) {
            try {
                Optional<WebSocketSession> wsSession = webSocketSessionRepository.getSession(userSession);
                if (wsSession.isPresent()) {
                    userSession.pushMessage(WebSocketContext.from(userSession, wsSession.get()), title, message);
                }
                successCount++;
            } catch (Exception e) {
                FhLogger.error(e);
            }
        }
        return successCount;
    }

    @Override
    public String getUserActiveFunctionality(String sessionId) {
        return userSessionRepository.getUserSessions().values().stream()
                .filter(session -> StringUtils.equal(sessionId, session.getConversationUniqueId()))
                .findAny()
                .map(session -> session.getUseCaseContainer().logStatePretty())
                .orElse(null);
    }

    private UserSession findUserSession(String sessionConversationUniqueId) {
        for (UserSession userSession : userSessionRepository.getUserSessions().values()) {
            if (userSession.getConversationUniqueId().equals(sessionConversationUniqueId)) {
                return userSession;
            }
        }
        return null;
    }

    private List<UserSession> findUserSessions(Set<String> sessionConversationUniqueIds) {
        return userSessionRepository.getUserSessions().values().stream()
                .filter(session -> sessionConversationUniqueIds.isEmpty() || sessionConversationUniqueIds.contains(session.getConversationUniqueId()))
                .collect(Collectors.toList());
    }
}
