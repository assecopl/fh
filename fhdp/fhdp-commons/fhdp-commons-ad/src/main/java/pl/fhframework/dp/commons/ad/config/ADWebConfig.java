package pl.fhframework.dp.commons.ad.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import pl.fhframework.config.FhWebConfiguration;
import pl.fhframework.core.logging.FhLogger;

import javax.servlet.Filter;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02/09/2020
 */
@Component
public class ADWebConfig implements FhWebConfiguration {

    @Value("${fhdp.security.provider.kerberos.enabled:false}")
    private boolean kerberosEnabled;

    private final AuthenticationEntryPoint spnegoEntryPoint;
    private final Filter spnegoAuthenticationProcessingFilter;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
//    private final LogoutHandler logoutHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;

    public ADWebConfig(AuthenticationEntryPoint spnegoEntryPoint, Filter spnegoAuthenticationProcessingFilter, AuthenticationFailureHandler authenticationFailureHandler, AuthenticationSuccessHandler authenticationSuccessHandler, LogoutSuccessHandler logoutSuccessHandler) {
        this.spnegoEntryPoint = spnegoEntryPoint;
        this.spnegoAuthenticationProcessingFilter = spnegoAuthenticationProcessingFilter;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
//        this.logoutHandler = logoutHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    @Override
    public void configure(HttpSecurity http) {
        try {
            http
                    .formLogin()
                    // Register the failure handler with log audit events
                    .failureHandler(authenticationFailureHandler)
                    // Register the success handler with log audit events
                    .successHandler(authenticationSuccessHandler)
                    .and()
                    .logout()
//                    // logout handler (vaadin push clean up)
//                    .addLogoutHandler(logoutHandler)
                    // logout success handler (audit log)
                    .logoutSuccessHandler(logoutSuccessHandler);


            if(kerberosEnabled) {
                // Additional filters for kerberos authentication
                http
                    // Spnego entry point
                    .exceptionHandling()
                    .authenticationEntryPoint(spnegoEntryPoint)
                    // Spnego entry point
                    .and()
                    .addFilterBefore(
                            spnegoAuthenticationProcessingFilter,
                            BasicAuthenticationFilter.class);
            }
        } catch (Exception e) {
            FhLogger.error(e);
            throw new RuntimeException(e);
        }

    }
}
