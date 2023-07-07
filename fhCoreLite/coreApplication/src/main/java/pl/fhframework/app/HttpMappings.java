package pl.fhframework.app;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import pl.fhframework.core.ResourceNotFoundException;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.resource.ImageRepository;
import pl.fhframework.core.resource.MarkdownRepository;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.dto.ForcedLogoutEvent;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Controller
public class HttpMappings {
    @Autowired
    private ImageRepository imageRepository;

    @Resource
    @Lazy
    private LocaleResolver localeResolver;

    @Autowired
    private MarkdownRepository markdownRepository;

    @Value("${fh.web.guests.allowed:false}")
    private boolean guestsAllowed;

    @Value("${server.logout.path:}")
    private String logoutPath;

    @Autowired
    @Lazy
    private MessageService messageService;

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource bundleMessageSource = new ReloadableResourceBundleMessageSource();
        bundleMessageSource.setBasenames("classpath:i18n/messages", "classpath:i18n/app_messages");
        bundleMessageSource.setDefaultEncoding("UTF-8");
        return bundleMessageSource;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            HttpServletRequest request, HttpServletResponse response) {

        Locale locale = getLocale(request, response);
        response.setLocale(locale);

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
//                Locale locale = getLocale(request, response);

                if (logout.equals(ForcedLogoutEvent.Reason.LOGOUT_TIMEOUT.getCode())) {
                    model.addObject("message", $("fh.infos.successfully_logged_out", locale));
                } else if (logout.equals(ForcedLogoutEvent.Reason.LOGOUT_FORCE.getCode())) {
                    model.addObject("message", $("fh.infos.successfully_logged_out_by_admin", locale));
                } else if (logout.equals(ForcedLogoutEvent.Reason.LOGOUT_NO_PERMISSION.getCode())) {
                    model.addObject("message", $("fh.infos.successfully_logged_out_no_permission", locale));
                } else {
                    model.addObject("message", $("fh.infos.successfully_logged_out", locale));
                }
            }
        }

        model.setViewName("login");

        return model;

    }

    @RequestMapping(value = "/${fh.web.guests.authenticate.path:authenticateGuest}", method = RequestMethod.GET)
    public ModelAndView authenticateGuest() {
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/");
        return model;
    }

    @RequestMapping(value = "/image", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity image(
            @WebParam(name = "module") String module,
            @WebParam(name = "path") String path) throws ResourceNotFoundException {
        Optional<Subsystem> subsystemOptional = ModuleRegistry.getLoadedModules().stream().filter(c -> c.getName().equals(module)).findAny();
        Subsystem subsystem = subsystemOptional.orElseThrow(ResourceNotFoundException::new);

        ImageRepository.ImageEntry resource = imageRepository.getImage(subsystem.getName(), path).orElseThrow(ResourceNotFoundException::new);

        return getFile(resource.getFile());
    }

    @RequestMapping(value = "/markdown", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity markdown(
            @WebParam(name = "module") String module,
            @WebParam(name = "path") String path) throws ResourceNotFoundException {
        Optional<Subsystem> subsystemOptional = ModuleRegistry.getLoadedModules().stream().filter(c -> c.getName().equals(module)).findAny();
        Subsystem subsystem = subsystemOptional.orElseThrow(ResourceNotFoundException::new);

        Optional<File> resource = markdownRepository.getFile(subsystem.getName(), path);
        resource.orElseThrow(ResourceNotFoundException::new);
        return getFile(resource.get());
    }

    @RequestMapping(value = "/sessionUsed", method = RequestMethod.GET)
    public ModelAndView sessionUsed(HttpServletRequest request) {

        ModelAndView model = new ModelAndView();

        model.setViewName("sessionUsed");

        return model;

    }

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/autologout", method = RequestMethod.GET)
    public ModelAndView timeout(@RequestParam(value = "reason", required = false) String reason,
                                HttpServletRequest request) {
        ModelAndView model = new ModelAndView();

        // todo: better condition for custom logout url
        if (!StringUtils.isNullOrEmpty(logoutPath) && !Objects.equals("logout", logoutPath)) {
            model.setViewName("redirect:/" + logoutPath);
            return model;
        }

        if (!StringUtils.isNullOrEmpty(reason)) {
            model.addObject("logout", reason);
        }
        model.setViewName("redirect:/login");

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

    private ResponseEntity getFile(File file) throws ResourceNotFoundException {

        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] bytes = FileUtils.getAllBytes(stream);
            return ResponseEntity.ok()
                    .lastModified(file.lastModified())
                    .contentLength(file.length())
                    .contentType(getFileContentType(file))
                    .body(bytes);
        } catch (IOException e) {
            FhLogger.error("Error while reading stream: " + e.getMessage(), e);
            throw new ResourceNotFoundException();
        }
    }

    private MediaType getFileContentType(File file) {
        if (FilenameUtils.isExtension(file.getName(), Arrays.asList("PNG", "png"))) {
            return MediaType.IMAGE_PNG;
        } else if (FilenameUtils.isExtension(file.getName(), Arrays.asList("GIF", "gif"))) {
            return MediaType.IMAGE_GIF;
        } else {
            return MediaType.IMAGE_JPEG;
        }
    }

    private String $(String key, Locale locale) {
        return messageService.getAllBundles().getMessage(key, null, locale, key);
    }
}
