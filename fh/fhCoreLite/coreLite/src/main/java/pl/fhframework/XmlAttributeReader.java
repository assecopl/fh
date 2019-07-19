package pl.fhframework;


import pl.fhframework.core.FhException;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

public class XmlAttributeReader {

    private XMLStreamReader reader;
    private Map<String, Object> cacheAttributes = new HashMap<>();

    public XmlAttributeReader(XMLStreamReader reader) {
        this.reader = reader;
    }

    public String getAttributeValue(String attributeName) {
        if (cacheAttributes.containsKey(attributeName)) {
            return (String) cacheAttributes.get(attributeName);
        }
        return reader.getAttributeValue("", attributeName);
    }

    /**
     * Read value of an attribute. If value is <code>null</code>, return defaultValue.
     *
     * @param attributeName
     * @param defaultValue
     * @return
     */
    public String getAttributeValueOrDefault(String attributeName, String defaultValue) {
        String attributeValue = getAttributeValue(attributeName);
        return (attributeValue != null) ? attributeValue : defaultValue;
    }

    /**
     * Add attribute to cache.
     *
     * @param id     - name of attribute
     * @param object - value
     */
    public void addAttributeAdHoc(String id, Object object) {
        cacheAttributes.put(id, object);
    }

    /**
     * Try parse value from attribute as a integer. If there is an error, return detault value.
     */
    public int getNumberOrDefault(String attributeName, int defaultValue) {
        String valueAsString = getAttributeValue(attributeName);
        if (valueAsString != null) {
            try {
                return Integer.parseInt(valueAsString);
            } catch (NumberFormatException nfe) {
                throw new FhException("Value is not a number for:" + reader.getLocation().getLineNumber() + ":" + reader.getLocation().getColumnNumber(), nfe);
            }
        } else {
            return defaultValue;
        }
    }
}