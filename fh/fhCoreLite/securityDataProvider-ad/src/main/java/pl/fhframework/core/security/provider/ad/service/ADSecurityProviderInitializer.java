package pl.fhframework.core.security.provider.ad.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.IDefaultUser;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.provider.service.AbstractSecurityProviderInitializer;
import pl.fhframework.core.security.provider.service.FhUserDetailsService;

import java.util.List;

@Service
public class ADSecurityProviderInitializer extends AbstractSecurityProviderInitializer {
    private final ActiveDirectoryLdapAuthenticationProvider authenticationProvider;

    @Autowired
    public ADSecurityProviderInitializer(ISecurityDataProvider securityDataProvider, ActiveDirectoryLdapAuthenticationProvider authenticationProvider, FhUserDetailsService fhUserDetailsService) {
        super(securityDataProvider);
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public void configureAuthentication(AuthenticationManagerBuilder auth, List<IDefaultUser> defaultUsers) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }
}
