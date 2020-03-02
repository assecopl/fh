package pl.fhframework.tools.loading;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Interface will be removed when FH will provied dynamic UC/Forms. But for now, this interface
 * helps for components like OutputLabel, sets value of if by body and not as an attribute in XML.
 * Example in usage:
 *
 * <OutputLabel> Some text to be displayd as value. </OutputLabel>
 */
public interface IBodyXml {

    /**
     * Sets in specific control a body of xml tag.
     *
     * @param body - string that is placed inside body tag.
     */
    void setBody(String body);

    /**
     * Returns name of the attribute that may be writen in XML body instead of an attribute.
     * @return property name or null
     */
    @JsonIgnore
    String getBodyAttributeName();

    /**
     * Checks if value of attribute pointed by getBodyAttributeName() should be written to body.
     * Defaults to true if has more than 50 chars, has multiple lines or has more than 4 escaped characters.
     *
     * @return true if should be written do body
     */
    default boolean shouldWriteToBody(String value) {
        if (value == null) {
            return false;
        }
        if (value.length() > 255) {
            return true;
        }
        if (value.contains("\n") || value.contains("\r")) {
            return true;
        }

        int specialCharCount = 0;
        for (int i = 0; i < value.length(); i++) {
            if (XmlUtils.isAttributeEncodedChar(value.charAt(i))) {
                specialCharCount++;
            }
        }
        if (specialCharCount > 4) {
            return true;
        }

        return false;
    }

}
