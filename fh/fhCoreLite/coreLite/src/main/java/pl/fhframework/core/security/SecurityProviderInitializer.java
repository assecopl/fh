package pl.fhframework.core.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import java.util.List;

/**
 * Interface of the security data provider.
 * @author tomasz.kozlowski (created on 2018-04-09)
 */
public interface SecurityProviderInitializer {

    /**
     * Method configures the application authentication security.
     *
     * @param auth security authentication manager builder.
     * @throws Exception a security configuration exception.
     */
    void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception;

    /**
     * Method configures the application authentication security with collection of default users.
     *
     * @param auth security authentication manager builder.
     * @param defaultUsers collection of default users.
     * @throws Exception a security configuration exception.
     */
    void configureAuthentication(AuthenticationManagerBuilder auth, List<IDefaultUser> defaultUsers) throws Exception;

}
