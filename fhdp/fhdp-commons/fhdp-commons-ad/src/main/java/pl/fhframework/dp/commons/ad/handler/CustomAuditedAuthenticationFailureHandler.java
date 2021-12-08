/*
 * KUD 2019.
 */
package pl.fhframework.dp.commons.ad.handler;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CustomAuditedAuthenticationFailureHandler
 *
 * @author <a href="mailto:pawel_kasprzak@skg.pl">Paweł Kasprzak</a>
 * @version $Revision: 12875 $, $Date: 2019-06-26 12:13:38 +0200 (Wed, 26 Jun
 * 2019) $
 */
public class CustomAuditedAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    public CustomAuditedAuthenticationFailureHandler(String defaultFailureUrl) {
        super(defaultFailureUrl);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
//        String sessionId = request.getSession() != null ? request.getSession().getId() : null;
//        String login = getLogin(request, exception, false);
//        if (getLogger().isTraceEnabled()) {
//            getLogger().error("Login \"" + login + "\" failure (session: " + sessionId + ", details: " + getHttpServletRequestInfo(request) + ") !", exception);
//        } else {
//            getLogger().error("Login: \"{}\" failure (session: {}, exception: {}) !", login, sessionId, exception.getMessage());
//        }
//        // log into audit log
//        logAboutSignIn(sessionId, request, exception);
        // call super (redirect with error URL)
        super.onAuthenticationFailure(request, response, exception);
    }

//    private void logAboutSignIn(String sessionId, HttpServletRequest request, org.springframework.security.core.AuthenticationException exception) {
//        try {
//            EventLogDto eventLogDto = new EventLogDto(
//                    EventLogTypeEnum.T, EventLogSeverityEnum.E, getErrorCode(exception), null, EventLogCategoryEnum.L);
//            eventLogDto.setUserLogin(getLogin(request, exception, true));
//            eventLogDto.setProcessId(sessionId);
//            eventLogDto.setShortDescription(getShortErrorDescription(exception));
//            eventLogDto.setDescription(getErrorDescription(request, exception));
//            EventLogResponse result = eventLog.log(eventLogDto);
//            if (!result.isValid()) {
//                throw new RuntimeException(result.getMessage());
//            }
//        } catch (Throwable e) {
//            getLogger().error("Authentication failure, error logging audit event !", e);
//        }
//    }
//
//    private String getLogin(HttpServletRequest request, org.springframework.security.core.AuthenticationException exception, boolean fullLogin) {
//        // default
//        String login = null;
//
//        if (SecurityUtils.isUserLoggedIn()) {
//            // from security context
//            return SecurityUtils.getUsername();
//        } else if (exception instanceof AuthenticationException) {
//            // from exception
//            return ((AuthenticationException) exception).getLogin();
//        } else {
//            // from request
//            for (String parameter : new String[]{"j_username", "username"}) {
//                login = request.getParameter(parameter);
//                if (login != null) {
//                    break;
//                }
//            }
//        }
//        if (fullLogin
//                && login != null
//                && !login.contains("@")
//                && ActiveDirectoryAuthenticationProviderOptions.hasConfiguredValue(ActiveDirectoryAuthenticationProviderOptions.Domain)) {
//            login = login + "@" + ActiveDirectoryAuthenticationProviderOptions.getConfiguredValue(ActiveDirectoryAuthenticationProviderOptions.Domain);
//        }
//        return login;
//    }
//
//    private String getErrorCode(org.springframework.security.core.AuthenticationException exception) {
//        String details = translationProvider.getString("application.login.error." + Introspector.decapitalize(exception.getClass().getSimpleName().replaceAll("Exception", "")) + ".code");
//        return !I18NProvider.isNoExistingKeyString(details) ? details : translationProvider.getString("application.login.error.general.code");
//    }
//
//    private String getShortErrorDescription(org.springframework.security.core.AuthenticationException exception) {
//        return translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.loginFailed");
//    }
//
//    private String getErrorDescription(HttpServletRequest request, org.springframework.security.core.AuthenticationException exception) {
//        String description = translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.loginFailed.reason",
//                getErrorDetails(exception))
//                + "\n" + translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.loginFailed.details",
//                        getRootMessage(exception))
//                + "\n" + translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.loginFailed.source",
//                        translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.loginFailed.source."
//                                + (isAuthenticationException(exception) ? "authentication" : "authorisation")))
//                + "\n" + translationProvider.getString("pl.gov.mf.kud.szprot.webapp.session.loginFailed.login", getLogin(request, exception, false))
//                + "\n" + getBrowserInfo(request);
//
//        return description;
//    }
//
//    private String getErrorDetails(org.springframework.security.core.AuthenticationException exception) {
//        String details = translationProvider.getString("application.login.error." + Introspector.decapitalize(exception.getClass().getSimpleName().replaceAll("Exception", "")));
//        return !I18NProvider.isNoExistingKeyString(details) ? details : translationProvider.getString("application.login.error.general");
//    }
//
//    private String getRootMessage(Throwable exception) {
//        return exception.getCause() != null ? getRootMessage(exception.getCause()) : exception.getMessage().replaceAll("\\x00", "");
//    }
//
//    private boolean isAuthenticationException(Throwable exception) {
//        return exception.getCause() != null ? isAuthenticationException(exception.getCause()) : exception instanceof javax.naming.NamingException;
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
//    private WebBrowser getBrowser(HttpServletRequest request) {
//        WebBrowser browser = new WebBrowser();
//        browser.updateRequestDetails(new VaadinServletRequest(request, null));
//        return browser;
//    }
}
