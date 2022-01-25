package pl.fhframework.dp.commons.ad.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.IDefaultUser;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.provider.service.AbstractSecurityProviderInitializer;
import pl.fhframework.core.security.provider.service.FhUserDetailsService;

import java.util.List;
import java.util.Objects;

@Service
public class ADSecurityProviderInitializer extends AbstractSecurityProviderInitializer {
    private final ActiveDirectoryLdapAuthenticationProvider adAuthenticationProvider;
    private final KerberosAuthenticationProvider kerberosAuthenticationProvider;
    private final KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider;
    private final FhUserDetailsService userDetailsService;
    private final ApplicationContext applicationContext;

    @Value("${fhdp.security.provider.kerberos.enabled:false}")
    private boolean kerberosEnabled;
    @Value("${fhdp.security.provider.local.enabled:false}")
    private boolean localAuthEnabled;
    @Value("${fhframework.security.provider.remote.pass.encode:true}")
    boolean passEncode;

    @Autowired
    public ADSecurityProviderInitializer(ISecurityDataProvider securityDataProvider,
                                         ActiveDirectoryLdapAuthenticationProvider adAuthenticationProvider,
                                         KerberosAuthenticationProvider kerberosAuthenticationProvider,
                                         KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider,
                                         FhUserDetailsService userDetailsService,
                                         ApplicationContext applicationContext) {
        super(securityDataProvider);
        this.adAuthenticationProvider = adAuthenticationProvider;
        this.kerberosAuthenticationProvider = kerberosAuthenticationProvider;
        this.kerberosServiceAuthenticationProvider = kerberosServiceAuthenticationProvider;
        this.userDetailsService = userDetailsService;
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureAuthentication(AuthenticationManagerBuilder auth, List<IDefaultUser> defaultUsers) throws Exception {
        if(kerberosEnabled) {
            auth.authenticationProvider(kerberosAuthenticationProvider);
            auth.authenticationProvider(kerberosServiceAuthenticationProvider);
        }
        auth.authenticationProvider(adAuthenticationProvider);
        if(localAuthEnabled) {
            auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(getPasswordEncoder());

            createDefaultAdminPermissions();
        }
    }

    /** Return password encoder instance */
    private PasswordEncoder getPasswordEncoder() {
        if (passEncode) {
            return applicationContext.getBean(PasswordEncoder.class);
        } else {
            return new PasswordEncoder() {
                @Override
                public String encode(CharSequence charSequence) {
                    return charSequence.toString();
                }

                @Override
                public boolean matches(CharSequence sequence1, String sequence2) {
                    return Objects.equals(sequence1.toString(), sequence2);
                }
            };
        }
    }
}
