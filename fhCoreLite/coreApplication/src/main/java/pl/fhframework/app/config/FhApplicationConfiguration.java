package pl.fhframework.app.config;

import pl.fhframework.config.IFhConfiguration;

/**
 * Created by pawel.ruta on 2018-05-09.
 */
public interface FhApplicationConfiguration extends IFhConfiguration {
    default String additionalCssUrl() {
        return null;
    }

    default String additionalJsUrl() {
        return null;
    }

    default String applicationTitle() {
        return null;
    }

    default String applicationLogoUrl() {
        return null;
    }

    default Boolean isMenuHidden() {
        return null;
    }
}
