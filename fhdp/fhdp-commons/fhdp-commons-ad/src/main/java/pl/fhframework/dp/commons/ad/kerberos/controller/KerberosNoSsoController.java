package pl.fhframework.dp.commons.ad.kerberos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.event.dto.ForcedLogoutEvent;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
public class KerberosNoSsoController {

    @Resource
    @Lazy
    private LocaleResolver localeResolver;

    @Value("${fh.web.guests.allowed:false}")
    private boolean guestsAllowed;


    @Autowired
    private MessageService messageService;

//    @Bean
//    public ReloadableResourceBundleMessageSource messageSource() {
//        ReloadableResourceBundleMessageSource bundleMessageSource = new ReloadableResourceBundleMessageSource();
//        bundleMessageSource.setBasename("classpath:i18n/messages");
//        bundleMessageSource.setDefaultEncoding("UTF-8");
//        return bundleMessageSource;
//    }

    @RequestMapping(value = "/login-no-sso", method = RequestMethod.GET)
    public ModelAndView login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            HttpServletRequest request, HttpServletResponse response) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("message",
                    getErrorMessage(request, response, "SPRING_SECURITY_LAST_EXCEPTION"));
        }

        if (logout != null) {
            if (guestsAllowed) {
                model.setViewName("redirect:/");
                return model;
            } else {
                Locale locale = getLocale(request, response);

                if (logout.equals(ForcedLogoutEvent.Reason.LOGOUT_TIMEOUT.getCode())) {
                    model.addObject("message", $("fh.infos.successfully_logged_out", locale));
                } else if (logout.equals(ForcedLogoutEvent.Reason.LOGOUT_FORCE.getCode())) {
                    model.addObject("message", $("fh.infos.successfully_logged_out_by_admin", locale));
                } else {
                    model.addObject("message", $("fh.infos.successfully_logged_out", locale));
                }
            }
        }

        model.setViewName("login-no-sso");

        return model;

    }

    //customize the error message
    private String getErrorMessage(HttpServletRequest request, HttpServletResponse response, String key) {

        Exception exception =
                (Exception) request.getSession().getAttribute(key);

        String error = "";
        Locale locale = getLocale(request, response);

        if (exception instanceof BadCredentialsException) {
            error = $("fh.errors.invalid_username_or_password", locale);
        } else if (exception instanceof LockedException) {
            error = $("fh.errors.account_is_blocked", locale);
        } else if (exception instanceof DisabledException) {
            error = $("fh.errors.account_is_blocked", locale);
        } else if (exception instanceof AccountExpiredException) {
            error = $("fh.errors.account_expired", locale);
        } else if (exception instanceof SessionAuthenticationException) {
            error = exception.getMessage();
        } else {
            error = $("fh.errors.invalid_username_or_password", locale);
        }

        return error;
    }

    private Locale getLocale(HttpServletRequest request, HttpServletResponse response) {
        return localeResolver.resolveLocale(request);
    }

    private String $(String key, Locale locale) {
        return messageService.getAllBundles().getMessage(key, null, locale, key);
    }
}
