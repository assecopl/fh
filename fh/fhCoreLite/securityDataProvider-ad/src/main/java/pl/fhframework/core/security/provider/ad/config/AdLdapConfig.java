package pl.fhframework.core.security.provider.ad.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

/**
 * @author tomasz.kozlowski (created on 15.06.2018)
 */
@Configuration
public class AdLdapConfig {

    @Value("${fhframework.security.provider.ad.server}")
    private String server;
    @Value("${fhframework.security.provider.ad.port}")
    private String port;
    @Value("${fhframework.security.provider.ad.domain}")
    private String domain;

    @Bean
    public ActiveDirectoryLdapAuthenticationProvider adLdapProvider() {
        final String url = String.format("ldap://%s:%s", server, port);

        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(domain, url);

        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);

        return provider;
    }
}
