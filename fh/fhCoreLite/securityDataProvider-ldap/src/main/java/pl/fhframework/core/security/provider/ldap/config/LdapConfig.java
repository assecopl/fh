package pl.fhframework.core.security.provider.ldap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * @author tomasz.kozlowski (created on 15.06.2018)
 */
@Configuration
public class LdapConfig {

    @Value("${fhframework.security.provider.ldap.server}")
    private String server;
    @Value("${fhframework.security.provider.ldap.port}")
    private String port;
    @Value("${fhframework.security.provider.ldap.domain}")
    private String domain;
    @Value("${fhframework.security.provider.ldap.user}")
    private String user;
    @Value("${fhframework.security.provider.ldap.password}")
    private String password;

    @Bean
    public LdapContextSource ldapContextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(String.format("ldap://%s:%s", server, port));
        contextSource.setBase(domain);
        contextSource.setUserDn(user);
        contextSource.setPassword(password);
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate(LdapContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }

}
