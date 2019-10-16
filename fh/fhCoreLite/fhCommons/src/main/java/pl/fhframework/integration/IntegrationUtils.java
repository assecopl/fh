package pl.fhframework.integration;

import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.stereotype.Service;
import pl.fhframework.core.FhException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by pawel.ruta on 2018-04-13.
 */
@Service
public class IntegrationUtils {
    public static boolean isValidUrl(String url) {
        URLEditor urlValidator = new URLEditor();
        try {
            urlValidator.setAsText(url);
        }
        catch (IllegalArgumentException iae) {
            return false;
        }
        return true;
    }

    public static String cleanUri(String uri) {
        return uri.replaceAll("/$", "");
    }

    public static boolean isAbsolute(String url) {
        final URI u;
        try {
            u = new URI(url.split("\\?")[0]);

            return u.isAbsolute();
        } catch (URISyntaxException e) {
            throw new FhException(e);
        }
    }
}
