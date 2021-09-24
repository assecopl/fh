package pl.fhframework.core.security.certauth;

import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;

/**
 * User detail service which creates user details for X.509 authenticated users (SSL mutual authentication).
 */
@Component
public class X509CertificateUserDetailService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        if (token.getCredentials() instanceof X509Certificate) {
            X509Certificate cert = (X509Certificate) token.getCredentials();
            return new X509CertificateUserDetails(token.getName(), cert);
        } else {
            String type = token.getCredentials() != null ? token.getCredentials().getClass().getName() : null;
            throw new UsernameNotFoundException("Not a valid certificate object passed in PreAuthenticatedAuthenticationToken: " + type);
        }
    }
}
