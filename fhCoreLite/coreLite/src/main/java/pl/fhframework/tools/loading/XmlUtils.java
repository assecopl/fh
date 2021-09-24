package pl.fhframework.tools.loading;

import java.util.HashMap;
import java.util.Map;

/**
 * XML Utility methods.
 * Created by Piotr on 2017-03-31.
 */
public class XmlUtils {

    private static final Map<Character, String> CHAR_ESCAPES = new HashMap<Character, String>() {{
        put('\'', "&apos;");
        put('\"', "&quot;");
        put('\n', "&#xA;");
        put('\r', "&#xD;");
        put('&', "&amp;");
        put('<', "&lt;");
        put('>', "&gt;");
    }};

    /**
     * Encodes XML attribute's value.
     * @param value valie
     * @return encoded value
     */
    public static String encodeAttribute(String value) {
        StringBuilder output = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char singleChar = value.charAt(i);
            String escape = CHAR_ESCAPES.get(singleChar);
            if (escape != null) {
                output.append(escape);
            } else {
                output.append(singleChar);
            }
        }
        return output.toString();
    }

    /**
     * Checks if a character in attribute value should be encoded.
     * @param singleChar single character
     * @return true if should be encoded
     */
    public static boolean isAttributeEncodedChar(char singleChar) {
        return CHAR_ESCAPES.containsKey(singleChar);
    }
}
