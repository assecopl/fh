package pl.fhframework.core.cloud.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.fhframework.ReflectionUtils;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Cloud server's fuctional definition exposed to cloud clients
 */
@Data
public class CloudServerDefinition {

    private List<ExposedMenuElement> exposedMenu = new ArrayList<>();

    private List<ExposedUseCaseDefinition> exposedUseCases = new ArrayList<>();

    private List<ExposedUseCaseDefinition> exposedRestServices = new ArrayList<>();

    private List<ExposedResourceAntMatcher> exposedResources = new ArrayList<>();

    public boolean checkUriExposed(String uri) {
        for (ExposedResourceAntMatcher matcher : exposedResources) {
            if (matcher.uriMatches(uri)) {
                return true;
            }
        }

        // todo: remove when Cloud IdP
        for (ExposedUseCaseDefinition rest : exposedRestServices) {
            for (String serviceInt : rest.getServiceInterfaces()) {
                Class<?> clazz = ReflectionUtils.getClassForName(serviceInt);
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + clazz.getAnnotation(RequestMapping.class).path()[0] + "/**");
                if (matcher.matches(Paths.get(uri))) {
                    return true;
                }
            }
        }
        return false;
    }
}
