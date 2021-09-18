package pl.fhframework.core.security.certauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import pl.fhframework.config.FhWebConfiguration;
import pl.fhframework.core.logging.FhLogger;

/**
 * Configures server side security for client SSL certificate authentication.
 */
@Component
public class CertAuthServerConfigurer implements FhWebConfiguration {

    @Autowired
    private X509CertificateUserDetailService userDetailService;

    @Value("${fhframework.certAuth.server.enabled:false}")
    private boolean enabled;

    @Override
    public void configure(HttpSecurity http) {
        if (enabled) {
            FhLogger.info(this.getClass(), "Enabling client SSL certificate authentication for this server");
            try {
                http.x509().subjectPrincipalRegex("CN=(.*?)(?:,|$)").authenticationUserDetailsService(userDetailService);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }
}
