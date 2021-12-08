package pl.fhframework.dp.commons.ad.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import pl.fhframework.core.logging.FhLogger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 31.01.2020
 */
public class ADPropertiesFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String uri = ((HttpServletRequest) servletRequest).getRequestURI();
        if (auth != null && auth.getPrincipal() != null
                && auth.getPrincipal() instanceof UserDetails &&
                (uri.endsWith("/") || uri.endsWith("logout"))) {
                FhLogger.info("request: {}\n Auth: {}", ((HttpServletRequest) servletRequest).getRequestURI(), (auth == null) ? "null" : auth.toString());
                FhLogger.info("Credentials: {}", auth.getCredentials());
                HttpSession session = ((HttpServletRequest) servletRequest).getSession(true);
                session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

//                FhLogger.info("username: {}", ((UserDetails) auth.getPrincipal()).getUsername());
//            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
//            httpResponse.sendRedirect("/vlp");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
