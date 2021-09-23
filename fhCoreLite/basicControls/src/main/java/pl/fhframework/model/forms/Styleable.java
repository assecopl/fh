package pl.fhframework.model.forms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by k.czajkowski on 16.01.2017.
 */
public interface Styleable {

    String ATTR_STYLE = "style";

    /**
     * Enum that holds bootstrap style classes names: default, primary, success, info, warning, danger, light.
     */
    enum Style {
        DEFAULT,
        PRIMARY,
        SUCCESS,
        INFO,
        WARNING,
        DANGER,
        LINK,
        LIGHT,
        DARK,
        SECONDARY;

        private static Map<String, Style> stylesMap = new HashMap<>(6);

        static {
            stylesMap.put("default", DEFAULT);
            stylesMap.put("primary", PRIMARY);
            stylesMap.put("success", SUCCESS);
            stylesMap.put("info", INFO);
            stylesMap.put("warning", WARNING);
            stylesMap.put("danger", DANGER);
            stylesMap.put("link", LINK);
            stylesMap.put("light", LIGHT);
            stylesMap.put("dark", DARK);
            stylesMap.put("secondary", SECONDARY);
        }

        @JsonValue
        public String toValue() {
            for (Map.Entry<String, Style> entry : stylesMap.entrySet()) {
                if (entry.getValue() == this) {
                    return entry.getKey();
                }
            }
            return null;
        }

        @JsonCreator
        /**
         * Returns Style enum for given String. If no Style is found, PRIMARY Style is returned.
         */
        public static Style forValue(String value) {
            Style style = stylesMap.get(value);
            return (style != null) ? style : Style.PRIMARY;
        }
    }
}
