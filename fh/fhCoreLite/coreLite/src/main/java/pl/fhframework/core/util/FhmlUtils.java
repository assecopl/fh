package pl.fhframework.core.util;

/**
 * Utility methods for FHML manipulation
 */
public class FhmlUtils {

    /**
     * Adds color to text
     * @param text text (may be empty or null)
     * @param color color name or HTML color code
     * @return FHML colorized text
     */
    public static String color(String text, String color) {
        if (text == null || text.isEmpty()) {
            return text;
        } else {
            return "[color='" + color + "']" + text + "[/color]";
        }
    }

    /**
     * Adds bold to text
     * @param text text (may be empty or null)
     * @return FHML bolded text
     */
    public static String bold(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        } else {
            return "[b]" + text + "[/b]";
        }
    }

    /**
     * Creates icon
     * @param icon icon (may be empty or null)
     * @return FHML icon
     */
    public static String icon(String icon) {
        if (icon == null || icon.isEmpty()) {
            return "";
        } else {
            return "[icon='" + icon + "']";
        }
    }

    /**
     * Changes text size
     * @param text text (may be empty or null)
     * @param size given size
     * @return FHML text with given size
     */
    public static String size(String text, int size) {
        if (text == null || text.isEmpty()) {
            return "";
        } else {
            return "[size='" + size + "']" + text + "[/size]";
        }
    }
}
