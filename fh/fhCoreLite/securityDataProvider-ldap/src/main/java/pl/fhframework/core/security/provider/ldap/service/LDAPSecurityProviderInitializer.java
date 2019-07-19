package pl.fhframework.core.security.provider.ldap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.IDefaultUser;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.provider.service.AbstractSecurityProviderInitializer;

import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2018-06-13)
 */
@Service
public class LDAPSecurityProviderInitializer extends AbstractSecurityProviderInitializer {

    @Value("${fhframework.security.provider.ldap.user-base}")
    private String userBase;
    @Value("${fhframework.security.provider.ldap.user-filter}")
    private String userFilter;
    @Value("${fhframework.security.provider.ldap.group-base}")
    private String groupBase;
    @Value("${fhframework.security.provider.ldap.group-filter}")
    private String groupFilter;
    @Value("${fhframework.security.provider.ldap.password-attribute}")
    private String passAttribute;

    private final LdapContextSource ldapContextSource;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LDAPSecurityProviderInitializer(ISecurityDataProvider securityDataProvider, LdapContextSource ldapContextSource, PasswordEncoder ldapPasswordEncoder) {
        super(securityDataProvider);
        this.ldapContextSource = ldapContextSource;
        this.passwordEncoder = ldapPasswordEncoder;
    }

    /**
     * Method configures the application authentication security.
     * NOTICE: LDAP Security Data Provider is an external data provider, thus this method does not create default users.
     *
     * @param auth security authentication manager builder.
     * @param defaultUsers param is not used by LDAP Security Data Provider.
     * @throws Exception a security configuration exception.
     */
    @Override
    public void configureAuthentication(AuthenticationManagerBuilder auth, List<IDefaultUser> defaultUsers) throws Exception {
        auth
            .ldapAuthentication()
                .userSearchBase(userBase)
                .userSearchFilter(userFilter)
                .groupSearchBase(groupBase)
                .groupSearchFilter(groupFilter)
                .rolePrefix("")
                .contextSource(ldapContextSource)
                .passwordCompare()
                    .passwordEncoder(passwordEncoder)
                    .passwordAttribute(passAttribute);

        createDefaultAdminPermissions();
    }

}
