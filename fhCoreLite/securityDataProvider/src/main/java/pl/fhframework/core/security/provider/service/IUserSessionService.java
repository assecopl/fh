package pl.fhframework.core.security.provider.service;

import org.springframework.core.io.Resource;
import pl.fhframework.core.security.provider.model.SessionInfo;

import java.util.List;

/**
 * Interface of a user session managing API.
 */
public interface IUserSessionService {

    /**
     * Lists user sessions
     * @return user sessions list
     */
    public List<SessionInfo> getUserSessions();

    /**
     * Forcibly logs out user session with given id
     * @param userSessionId user session id
     * @return True, if user has been successfully logged off. False, if exception has occurred or session does not exists.
     */
    public boolean forceLogout(String userSessionId);

    /**
     * Downloads log associated with user session with given id
     * @param userSessionId user session id
     * @return log file resource
     */
    public Resource donwloadUserLog(String userSessionId);

    /**
     * Displays a message to users. Message may include new line characters.
     * @param userSessionIds user session ids or empty list if should be sent to all
     * @param title title
     * @param message message
     * @return count of sessions that message was successfully send to
     */
    public int sendMessage(List<String> userSessionIds, String title, String message);

}
