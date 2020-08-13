package pl.fhframework.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Paweł Ruta
 */
public class SecurityFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    SingleLoginLockManager singleLoginLockManager;

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
            throws IOException, ServletException {
        if (req instanceof HttpServletRequest &&
                super.requiresAuthentication((HttpServletRequest) req, (HttpServletResponse) res)) {
            ((HttpServletRequest) req).getSession().setAttribute("baseUrl", getBaseUrl(
                    (HttpServletRequest) req));
            if (singleLoginLockManager.isLoggedIn(obtainUsername((HttpServletRequest) req))) {
                unsuccessfulAuthentication((HttpServletRequest) req, (HttpServletResponse) res, new SessionAuthenticationException("Użytkownik jest już zalogowany"));
            }
            else {
                super.doFilter(req, res, chain);
            }
        } else {
            super.doFilter(req, res, chain);
        }
    }

    @Override
    protected boolean requiresAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        return false;
    }

    @Autowired
    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    private String getBaseUrl(final HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String uri = request.getRequestURI();
        String ctx = request.getContextPath();
        String base = url.substring(0, url.length() - uri.length() + ctx.length()) + "/";

        return base;
    }
}