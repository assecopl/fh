/*
 * KUD 2020.
 */
package pl.fhframework.dp.commons.ad.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CustomAuditedLogoutSuccessHandler
 *
 * @author <a href="mailto:pawel_kasprzak@skg.pl">Paweł Kasprzak</a>
 * @version $Revision: 12327 $, $Date: 2020-01-10 17:55:41 +0100 (Fri) $
 */
public class CustomAuditedLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String sessionId = request.getRequestedSessionId() != null
                ? request.getRequestedSessionId()
                : request.getSession() != null ? request.getSession().getId() : null;
        Object principal = authentication != null ? authentication.getPrincipal() : null;
//        String login;
//        if (principal instanceof Principal) {
//            login = ((Principal) principal).getUser().getLogin();
//        } else {
//            login = principal != null ? principal.toString() : null;
//        }
//        boolean byUser = "u".equalsIgnoreCase(request.getParameter("type"));
//        if (getLogger().isTraceEnabled()) {
//            // TODO add more detailed trace
//            getLogger().trace("User \"{}\" has successfully signed out (session: {}, origin: {}).", login, sessionId, byUser ? "User" : "System");
//        } else {
//            getLogger().info("User \"{}\" has successfully signed out (session: {}, origin: {}).", login, sessionId, byUser ? "User" : "System");
//        }
//        if (sessionId != null
//                && login != null) {
//            // remove session
//            removeSession(sessionId);
//            // log into audit log
//            // TODO add request parameter with optional sign out reason (permissions, service mode etc.)
//            logAboutSignOut(sessionId, login, byUser, null);
//        }
        // call super (redirect with logout success URL)
        super.onLogoutSuccess(request, response, authentication);
    }

//    private void removeSession(String sessionId) {
//        sessionsStore.removeSession(sessionId);
//    }
//
//    private void logAboutSignOut(String sessionId, String login, boolean byUser, String description) {
//        try {
//            EventLogDto eventLogDto
//                    = new EventLogDto(EventLogTypeEnum.T,
//                            EventLogSeverityEnum.I,
//                            byUser
//                                    ? translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.logoutByUser.code")
//                                    : translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.logoutBySystem.code"),
//                            null,
//                            EventLogCategoryEnum.L);
//            eventLogDto.setUserLogin(login);
//            eventLogDto.setProcessId(sessionId);
//            eventLogDto.setShortDescription(byUser
//                    ? translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.logoutByUser")
//                    : translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.logoutBySystem"));
//            eventLogDto.setDescription(StringUtils.isNotEmpty(description) ? description : eventLogDto.getShortDescription());
//            eventLog.log(eventLogDto);
//        } catch (Throwable e) {
//            getLogger().error("Logout success, error logging audit event !", e);
//        }
//    }
}
