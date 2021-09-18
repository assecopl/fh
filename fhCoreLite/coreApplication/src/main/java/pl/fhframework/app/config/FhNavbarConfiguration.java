package pl.fhframework.app.config;

import pl.fhframework.config.IFhConfiguration;

import java.util.Map;

/**
 * Created by pawel.ruta on 2018-05-09.
 */
public interface FhNavbarConfiguration extends IFhConfiguration {
    String BOOTSWATCH_THEME_URL = "https://bootswatch.com/4/%s/bootstrap.css";
    String FH_CSS = "fh";
    String MATERIA_CSS = "materia";
    String BASE_CSS = "default";

    /**
     * Return customized list of css <id, url> available within Navigation Bar
     *
     * @param defaultCssUrls map with fh default css styles
     *
     * @return list of css <id, url>
     */
    default Map<String, String> bootstrapCssUrlList(Map<String, String> defaultCssUrls) {
        return defaultCssUrls;
    }

    default String defaultCss() {
        return null;
    }
}
