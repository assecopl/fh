package pl.fhframework.core.session;

import pl.fhframework.UserSession;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Interface of a UserSession finder for REST controllers.
 */
public interface IRestUserSessionFinder {

    Optional<UserSession> getUserSession(HttpServletRequest httpServletRequest);
}
