package pl.fhframework.core.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.UserSession;
import pl.fhframework.WebSocketSessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.Set;

/**
 * UserSession finder for REST controllers which uses HttpSession attribute.
 */
@Component
@Slf4j
public class HttpSessionUserSessionFinder implements IRestUserSessionFinder {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Override
    public Optional<UserSession> getUserSession(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession != null) {
            Set<UserSession> userSessions = userSessionRepository.getUserSessionsInHttpSession(httpSession);
            //If there is only one user session in the http session we simple return it
            if (userSessions==null || userSessions.isEmpty()) {
                return Optional.empty();
            }else if (userSessions.size() == 1) {
                return Optional.of(userSessions.iterator().next());
            } else {
                //If there is more than one user session in the http session we need to check if we pick some in httpServerletRequest header
                String webSocketSessionId = httpServletRequest.getHeader(UserSession.WEB_SOCKET_SESSION_ID);
                if (webSocketSessionId != null) {
                    Optional<UserSession> foundUserSession = userSessionRepository.getUserSessionsInHttpSession(httpSession).stream()
                            .filter(userSession -> userSession.getConversationId().equals(webSocketSessionId))
                            .findFirst();
                    if (foundUserSession.isPresent()) {
                        return foundUserSession;
                    }
                }
                //If there is no header WEB_SOCKET_SESSION_ID or no user session is matched we pick the first user session in the http session
                log.warn("No header " + UserSession.WEB_SOCKET_SESSION_ID + " or no userSession is matched so I pick the first userSession in the current http session. It could be wrong if there is more than one userSession/windows in the sane http session.");
                return Optional.of(userSessions.iterator().next());
            }
            //return Optional.ofNullable(userSessionRepository.getUserSession(httpSession));
        } else {
            return Optional.empty();
        }
    }
}
