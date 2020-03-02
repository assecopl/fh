package pl.fhframework.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.fhframework.core.ResourceNotFoundException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.resource.ImageRepository;
import pl.fhframework.core.resource.MarkdownRepository;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.dto.ForcedLogoutEvent;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by krzysztof.kobylarek on 2017-05-17.
 */
@Controller
@EnableWebMvc
public class HttpMappings extends WebMvcConfigurerAdapter {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private MarkdownRepository markdownRepository;

    @Value("${fh.web.guests.allowed:false}")
    private boolean guestsAllowed;

    @Value("${server.logout.path:}")
    private String logoutPath;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            HttpServletRequest request) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("message",
                    getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
        }

        if (logout != null) {
            if (guestsAllowed) {
                model.setViewName("redirect:/");
                return model;
            } else {
                if (logout.equals(ForcedLogoutEvent.Reason.LOGOUT_TIMEOUT.getCode())) {
                    model.addObject("message", "Nastąpiło automatyczne wylogowanie.");
                } else if (logout.equals(ForcedLogoutEvent.Reason.LOGOUT_FORCE.getCode())) {
                    model.addObject("message", "Nastąpiło wylogowanie z polecenia administratora.");
                } else {
                    model.addObject("message", "Nastąpiło poprawne wylogowanie.");
                }
            }
        }

        model.setViewName("login");

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

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/"};

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(
                    CLASSPATH_RESOURCE_LOCATIONS);
        }
    }

    //customize the error message
    private String getErrorMessage(HttpServletRequest request, String key) {

        Exception exception =
                (Exception) request.getSession().getAttribute(key);

        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Błędna nazwa użytkownika lub hasło.";
        } else if (exception instanceof LockedException) {
            error = "Konto jest zablokowane, skontaktuj się z administratorem.";
        } else if (exception instanceof DisabledException) {
            error = "Konto jest zablokowane, skontaktuj się z administratorem.";
        } else if (exception instanceof AccountExpiredException) {
            error = "Konto wygasło.";
        } else if (exception instanceof SessionAuthenticationException) {
            error = exception.getMessage();
        } else {
            error = "Błędna nazwa użytkownika lub hasło.";
        }

        return error;
    }

    private ResponseEntity getFile(File file) throws ResourceNotFoundException {

        try (FileInputStream stream = new FileInputStream(file)) {
            byte[] bytes = FileUtils.getAllBytes(stream);
            return ResponseEntity.ok()
                    .lastModified(file.lastModified())
                    .contentLength(file.length())
                    .body(bytes);
        } catch (IOException e) {
            FhLogger.error("Error while reading stream: " + e.getMessage(), e);
            throw new ResourceNotFoundException();
        }
    }
}
