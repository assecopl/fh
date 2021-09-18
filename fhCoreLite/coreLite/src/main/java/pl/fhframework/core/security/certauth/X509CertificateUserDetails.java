package pl.fhframework.core.security.certauth;

import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Optional;

/**
 * X.509 certificate authenticated user details.
 */
public class X509CertificateUserDetails extends User {

    public static final String CERTIFICATE_AUTHENTICATED_USER_AUTHORITY = "CERTIFICATE_AUTHENTICATED_USER";

    @Getter
    private X509Certificate certificate;

    @Getter
    private String dn;

    public X509CertificateUserDetails(String username, X509Certificate certificate) {
        super(username, "", AuthorityUtils.commaSeparatedStringToAuthorityList(CERTIFICATE_AUTHENTICATED_USER_AUTHORITY));
        this.certificate = certificate;
        this.dn = this.certificate.getSubjectX500Principal().getName();
    }

    public String getCn() {
        return getUsername();
    }

    public static Optional<X509CertificateUserDetails> extractSSLAuth(Principal principal) {
        if (principal instanceof PreAuthenticatedAuthenticationToken) {
            PreAuthenticatedAuthenticationToken token = (PreAuthenticatedAuthenticationToken) principal;
            if (token.getPrincipal() instanceof X509CertificateUserDetails) {
                return Optional.of((X509CertificateUserDetails) token.getPrincipal());
            }
        }
        return Optional.empty();
    }
}
