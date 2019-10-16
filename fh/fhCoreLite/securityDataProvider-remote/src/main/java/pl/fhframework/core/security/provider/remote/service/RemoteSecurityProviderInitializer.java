package pl.fhframework.core.security.provider.remote.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.IDefaultUser;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.provider.service.AbstractSecurityProviderInitializer;
import pl.fhframework.core.security.provider.service.LunaUserDetailsService;

import java.util.List;
import java.util.Objects;

/**
 * @author Tomasz Kozlowski (created on 20.05.2019)
 */
@Service
public class RemoteSecurityProviderInitializer extends AbstractSecurityProviderInitializer {

    @Value("${luna.security.provider.remote.pass.encode:true}")
    boolean passEncode;

    private final LunaUserDetailsService userDetailsService;
    private final ApplicationContext applicationContext;

    @Autowired
    public RemoteSecurityProviderInitializer(ISecurityDataProvider securityDataProvider, LunaUserDetailsService userDetailsService, ApplicationContext applicationContext) {
        super(securityDataProvider);
        this.userDetailsService = userDetailsService;
        this.applicationContext = applicationContext;
    }

    /**
     * Method configures the application authentication security.
     * NOTICE: Remote Security Data Provider is an external data provider, thus this method does not create default users.
     *
     * @param auth security authentication manager builder.
     * @param defaultUsers param is not used by Remote Security Data Provider.
     * @throws Exception a security configuration exception.
     */
    @Override
    public void configureAuthentication(AuthenticationManagerBuilder auth, List<IDefaultUser> defaultUsers) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(getPasswordEncoder());

        createDefaultAdminPermissions();
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
