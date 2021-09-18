package pl.fhframework.core.security.provider.management.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.certauth.X509CertificateUserDetails;
import pl.fhframework.core.security.provider.management.api.ManagementAPIController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Management API
 */
@Service
@ConditionalOnProperty(name = "fhframework.managementApi.enabled", havingValue = "true")
public class ManagementAPIAuthInterceptor extends HandlerInterceptorAdapter {

    private static final Set<String> NOT_LOGGED_URLS = new HashSet<>(Arrays.asList(
            ManagementAPIController.MANAGEMENT_API_STATUS_URI
    ));

    @Autowired
    private ManagementAPISecurityConfig securityConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<X509CertificateUserDetails> x509Cert = null;
        if (securityConfig.isAuthorizationEnabled()) {
            x509Cert = X509CertificateUserDetails.extractSSLAuth(request.getUserPrincipal());
            if (!x509Cert.isPresent()) {
                FhLogger.error("Not a X.509 authenticated user from {} is trying to access {}",
                        request.getRemoteAddr(), request.getRequestURI());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Must be a X.509 authenticated client");
                return false;
            }
            if (!securityConfig.getAuthorizedClients().contains(x509Cert.get().getCn())) {
                FhLogger.error("X.509 authenticated user {} from {} is trying to access {} is not autorized to use manament API",
                        x509Cert.get().getDn(), request.getRemoteAddr(), request.getRequestURI());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "X.509 authenticated client '" + x509Cert.get().getCn() + "' is not auhorized to use management API");
                return false;
            }
        }
        boolean useLogging = !NOT_LOGGED_URLS.stream().anyMatch(url -> request.getRequestURI().startsWith(url));
        if (useLogging) {
            if (x509Cert != null && x509Cert.isPresent()) {
                FhLogger.info(this.getClass(), "X.509 authenticated user {} from {} is using {}",
                        x509Cert.get().getDn(), request.getRemoteAddr(), request.getRequestURI());
            }
            else {
                FhLogger.info(this.getClass(), "User {} from {} is using {}",
                        request.getUserPrincipal().getName(), request.getRemoteAddr(), request.getRequestURI());
            }
        }
        return true;
    }
}
