package pl.fhframework.core.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.UserSession;
import pl.fhframework.WebSocketSessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * UserSession finder for REST controllers which uses HttpSession attribute.
 */
@Component
public class HttpSessionUserSessionFinder implements IRestUserSessionFinder {

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Override
    public Optional<UserSession> getUserSession(HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession != null) {
            return Optional.ofNullable (userSessionRepository.getUserSession(httpSession));
        } else {
            return Optional.empty();
        }
    }
}
