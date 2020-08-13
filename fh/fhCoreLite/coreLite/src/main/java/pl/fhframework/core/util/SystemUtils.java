package pl.fhframework.core.util;

import pl.fhframework.core.logging.FhLogger;

public class SystemUtils {

    public static final String LINE_SEPARATOR = getSystemProperty("line.separator");

    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException ex) {
            // we are not allowed to look at this property
            FhLogger.error("Caught a SecurityException reading the system property '" + property
                    + "'; the SystemUtils property value will default to null.");
            return null;
        }
    }

}
