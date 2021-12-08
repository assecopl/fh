/*
 * KUD 2019.
 */
package pl.fhframework.dp.commons.ad.handler;


import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CustomAuditedAuthenticationSuccessHandler
 *
 * @author <a href="mailto:pawel_kasprzak@skg.pl">Paweł Kasprzak</a>
 * @version $Revision: 17254 $, $Date: 2019-07-10 07:29:23 +0200 (śr., 10 lip
 * 2019) $
 */
public class CustomAuditedAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
//        String sessionId = request.getSession() != null ? request.getSession().getId() : null;
//        Object principal = authentication != null ? authentication.getPrincipal() : null;
//        String login;
//        if (principal instanceof Principal) {
//            login = ((Principal) principal).getUser().getLogin();
//        } else {
//            login = principal != null ? principal.toString() : null;
//        }
//        if (getLogger().isTraceEnabled()) {
//            getLogger().trace("User \"{}\" has successfully signed in (session: {}, details: {}).", login, sessionId, getHttpServletRequestInfo(request));
//        } else {
//            getLogger().info("User \"{}\" has successfully signed in (session: {}).", login, sessionId);
//        }
        super.onAuthenticationSuccess(request, response, authentication);
//        if (sessionId != null
//                && login != null) {
//            // store session
//            storeSession(sessionId, login, request);
//            // log into audit log
//            logAboutSignIn(login, request);
//        }
    }

//    private void storeSession(String sessionId, String login, HttpServletRequest request) {
//        SessionStore<LightweightVaadinSession> store = new SessionStore();
//        store.setId(sessionId);
//        store.setLogin(login);
//        store.setCreationTime(request.getSession().getCreationTime());
//        store.setLastAccessedTime(request.getSession().getLastAccessedTime());
//        store.getClientInfo().setAddress(getClientAddress(request));
//        store.getClientInfo().setRemoteAddress(getRequestAddress(request));
//        WebBrowser browser = getBrowser(request);
//        if (browser != null) {
//            store.getClientInfo().setLocale(browser.getLocale());
//            store.getClientInfo().setBrowserApplication(browser.getBrowserApplication());
//            store.getClientInfo().setSecureConnection(browser.isSecureConnection());
//        }
//        store.getServerInfo().setAddress(request.getLocalAddr());
//        store.getServerInfo().setName(request.getLocalName());
//        sessionsStore.addSession(store);
//    }
//
//    private void logAboutSignIn(String login, HttpServletRequest request) {
//        try {
//            EventLogDto eventLogDto = new EventLogDto(
//                    EventLogTypeEnum.T, EventLogSeverityEnum.I, "L1", null, EventLogCategoryEnum.L);
//            eventLogDto.setUserLogin(login);
//            eventLogDto.setProcessId(request.getSession() != null
//                    ? request.getSession().getId()
//                    : null);
//            eventLogDto.setShortDescription(translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.loginSuccess"));
//            eventLogDto.setDescription(getBrowserInfo(request));
//            EventLogResponse result = eventLog.log(eventLogDto);
//            if (!result.isValid()) {
//                throw new RuntimeException(result.getMessage());
//            }
//        } catch (Throwable e) {
//            getLogger().error("Authentication success, error logging audit event !", e);
//        }
//    }
//
//    private String getHttpServletRequestInfo(HttpServletRequest request) {
//        StringBuilder req = new StringBuilder(request.getClass().getName());
//        req.append(" [");
//        req.append("authType: ").append(request.getAuthType());
//        req.append(", characterEncoding: ").append(request.getCharacterEncoding());
//        req.append(", locale: ").append(request.getLocale());
//        req.append(", contentLength: ").append(request.getContentLength());
//        req.append(", contextPath: ").append(request.getContextPath());
//        req.append(", protocol: ").append(request.getProtocol());
//        req.append(", localAddr: ").append(request.getLocalAddr());
//        req.append(", localName: ").append(request.getLocalName());
//        req.append(", localPort: ").append(request.getLocalPort());
//        req.append(", remoteAddr: ").append(request.getRemoteAddr());
//        req.append(", remoteHost: ").append(request.getRemoteHost());
//        req.append(", remotePort: ").append(request.getRemotePort());
//        req.append(", remoteUser: ").append(request.getRemoteUser());
//        req.append(", headers: ").append("{");
//        Enumeration<String> headerNames = request.getHeaderNames();
//        boolean first = true;
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            if (first) {
//                first = false;
//            } else {
//                req.append(", ");
//            }
//            req.append(headerName).append(": ").append(request.getHeader(headerName));
//        }
//        req.append("}");
//        req.append("]");
//        return req.toString();
//    }
//
//    private String getBrowserInfo(HttpServletRequest request) {
//        WebBrowser browser = getBrowser(request);
//        return browser != null ? translationProvider
//                .getString("pl.gov.mf.kud.szprot.webapp.session.browserInfo",
//                        getClientAddress(request), browser.getBrowserApplication(), browser.getBrowserApplication())
//                : null;
//    }
//
//    private String getClientAddress(HttpServletRequest request) {
//        if (request != null) {
//            WebBrowser browser = getBrowser(request);
//            String[] clientAddresses = new String[]{
//                request.getHeader("Proxy-Client-IP"),
//                request.getHeader("X-Forwarded-For"),
//                browser != null ? browser.getAddress() : null};
//            for (String clientAddress : clientAddresses) {
//                if (StringUtils.isNotEmpty(clientAddress)) {
//                    return clientAddress;
//                }
//            }
//        }
//        return null;
//    }
//
//    private String getRequestAddress(HttpServletRequest request) {
//        if (request != null) {
//            String[] clientAddresses = new String[]{
//                request.getHeader("X-Forwarded-Host"),
//                request.getServerName()};
//            for (String clientAddress : clientAddresses) {
//                if (StringUtils.isNotEmpty(clientAddress)) {
//                    return clientAddress;
//                }
//            }
//        }
//        return null;
//    }
//
//    private WebBrowser getBrowser(HttpServletRequest request) {
//        WebBrowser browser = new WebBrowser();
//        browser.updateRequestDetails(new VaadinServletRequest(request, null));
//        return browser;
//    }
}
