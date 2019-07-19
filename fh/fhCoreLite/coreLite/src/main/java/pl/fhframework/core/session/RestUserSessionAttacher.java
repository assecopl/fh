package pl.fhframework.core.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * Interceptor for REST controllers that attaches UserSession object to SessionManager.
 * UserSession is taken from available IRestUserSessionFinder implementations.
 */
@Component
public class RestUserSessionAttacher implements HandlerInterceptor {

    private static final String MARKER_ATTRIBUTE = "RestUserSessionAttacher.marker";

    @Autowired
    private List<IRestUserSessionFinder> sessionFinders;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        for (IRestUserSessionFinder sessionFinder : sessionFinders) {
            Optional<UserSession> userSession = sessionFinder.getUserSession(httpServletRequest);
            if (userSession.isPresent()) {
                SessionManager.registerThreadSessionManager(new SessionHoldingSessionManager(userSession.get()));
                httpServletRequest.setAttribute(MARKER_ATTRIBUTE, true);
                break;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        if (httpServletRequest.getAttribute(MARKER_ATTRIBUTE) != null) {
            SessionManager.unregisterThreadSessionManager();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
