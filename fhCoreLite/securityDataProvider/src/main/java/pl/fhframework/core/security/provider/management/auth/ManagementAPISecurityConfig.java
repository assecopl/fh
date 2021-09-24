package pl.fhframework.core.security.provider.management.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.stereotype.Component;
import pl.fhframework.config.FhWebConfiguration;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.certauth.X509CertificateUserDetails;
import pl.fhframework.core.security.provider.management.api.ManagementAPIController;
import pl.fhframework.core.security.provider.management.api.SecurityManagementAPIController;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Configures URL security for management API
 */
@Component
@ConditionalOnProperty(name = "fhframework.managementApi.enabled", havingValue = "true")
public class ManagementAPISecurityConfig implements FhWebConfiguration {

    @Getter
    @Value("${fhframework.managementApi.certAuth.enabled:false}")
    private boolean authorizationEnabled;

    @Value("${fhframework.managementApi.certAuth.clients:}")
    private String authorizedClientsProp;

    @Getter
    private Set<String> authorizedClients;

    @PostConstruct
    public void init() {
        if (!authorizedClientsProp.isEmpty()) {
            authorizedClients = new HashSet<>(Arrays.asList(authorizedClientsProp.split(",")));
        } else {
            authorizedClients = Collections.emptySet();
        }
    }

    @Override
    public void configure(HttpSecurity http) {
        try {
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.AuthorizedUrl urls = http.authorizeRequests().antMatchers(
                            ManagementAPIController.MANAGEMENT_API_URI + "/**",
                            SecurityManagementAPIController.SECURITY_MANAGEMENT_API_URI + "/**");
            if (authorizationEnabled) {
                FhLogger.info(this.getClass(), "ManagementAPI secured with X.509 certificates");
                urls.hasAuthority(X509CertificateUserDetails.CERTIFICATE_AUTHENTICATED_USER_AUTHORITY);
            } else {
                FhLogger.error("ManagementAPI NOT SECURED");
                urls.permitAll();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
