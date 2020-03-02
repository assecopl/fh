package pl.fhframework.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by pawel.ruta on 2018-05-09.
 */
@Configuration
public class DefaultApplicationConfigurer {
    @Autowired(required = false)
    private FhApplicationConfiguration configuration;

    @Autowired(required = false)
    private FhNavbarConfiguration navbarConfiguration;

    private Map<String, String> cssUrls;

    @PostConstruct
    public void config() {
        if (configuration != null) {
            if (configuration.additionalCssUrl() != null) {
                System.setProperty("fh.application.css.url", configuration.additionalCssUrl());
            }
            if (configuration.additionalJsUrl() != null) {
                System.setProperty("fh.application.js.url", configuration.additionalJsUrl());
            }
            if (configuration.applicationTitle() != null) {
                System.setProperty("fh.application.title", configuration.applicationTitle());
            }
            if (configuration.applicationLogoUrl() != null) {
                System.setProperty("fh.application.logo.url", configuration.applicationLogoUrl());
            }
            if (Objects.equals(Boolean.TRUE, configuration.isMenuHidden())) {
                System.setProperty("fh.application.menu.hide", "true");
            }
        }

        cssUrls = getDefaultCssUrls();
        if (navbarConfiguration != null) {
            cssUrls = navbarConfiguration.bootstrapCssUrlList(cssUrls);

            if (navbarConfiguration.defaultCss() != null) {
                System.setProperty("fh.application.default.css", navbarConfiguration.defaultCss());

            }
        }
    }

    public Map<String, String> getCssUrls() {
        return cssUrls;
    }

    private Map<String, String> getDefaultCssUrls() {
        Map<String, String> cssUrls = new LinkedHashMap<>();
        cssUrls.put(FhNavbarConfiguration.FH_CSS, null);
        cssUrls.put(FhNavbarConfiguration.BASE_CSS, null);

        cssUrls.put("sketchy", String.format(FhNavbarConfiguration.BOOTSWATCH_THEME_URL, "sketchy"));
        cssUrls.put("cyborg", String.format(FhNavbarConfiguration.BOOTSWATCH_THEME_URL, "cyborg"));
        cssUrls.put("superhero", String.format(FhNavbarConfiguration.BOOTSWATCH_THEME_URL, "superhero"));

        return cssUrls;
    }
}
