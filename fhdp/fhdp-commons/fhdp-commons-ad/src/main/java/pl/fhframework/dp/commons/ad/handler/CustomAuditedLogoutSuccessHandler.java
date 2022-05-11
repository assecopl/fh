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

public class CustomAuditedLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String sessionId = request.getRequestedSessionId() != null
                ? request.getRequestedSessionId()
                : request.getSession() != null ? request.getSession().getId() : null;
        Object principal = authentication != null ? authentication.getPrincipal() : null;
        super.onLogoutSuccess(request, response, authentication);
    }

}
