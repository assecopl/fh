package pl.fhframework.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.fhframework.core.FhException;
import pl.fhframework.core.util.JsonUtil;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Gabriel on 2015-12-14.
 */
@Getter
@Setter
public class ValueChange {

    public static final String MAIN_VALUE_ATTRIBUTE = "value";

    private String formId;
    private String fieldId;
    private Map<String, Object> changedAttributes;

    public ValueChange() {
    }

    public ValueChange(String formId, String fieldId, Map<String, Object> changedAttributes) {
        this.formId = formId;
        this.fieldId = fieldId;
        this.changedAttributes = changedAttributes;
    }

    public boolean hasAttributeChanged(String name) {
        return changedAttributes.containsKey(name);
    }

    public boolean hasMainValueChanged() {
        return hasAttributeChanged(MAIN_VALUE_ATTRIBUTE);
    }

    /**
     * Convenience method for calling getStringAttribute(ValueChange.MAIN_VALUE_ATTRIBUTE).
     * Should not be used for controls operating on more than one output attribute.
     * @return new main value
     */
    @JsonIgnore
    public String getMainValue() {
        if (!hasMainValueChanged()) {
            throw new FhException(String.format(
                    "%s attribute has not changed. This form element is not using main value attribute! Changed attributes: %s",
                    MAIN_VALUE_ATTRIBUTE, String.valueOf(changedAttributes)));
        }
        return getStringAttributeWithCast(MAIN_VALUE_ATTRIBUTE);
    }

    public <T> T getObjectFromMapAttributes(String name, Class<T> clazz) {
        Map object = getAttribute(name, Map.class);
        if (object == null) {
            return null;
        } else {
            return JsonUtil.readValue(object, clazz);
        }
    }

    public List<Map<String, Object>> getMapListAttribute(String name) {
        return (List<Map<String, Object>>) getAttribute(name, List.class);
    }

    public List<String> getStringListAttribute(String name) {
        return (List<String>) getAttribute(name, List.class);
    }

    public Double getDoubleAttribute(String name) {
        return getAttribute(name, Double.class);
    }

    public String getStringAttribute(String name) {
        return getAttribute(name, String.class);
    }

    public String getStringAttributeWithCast(String name) {
        Object value = getChangedAttributes().get(name);
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else {
            return String.valueOf(value);
        }
    }

    public Integer getIntAttribute(String name) {
        return getAttribute(name, Integer.class);
    }

    public Boolean getBooleanAttribute(String name) {
        return getAttribute(name, Boolean.class);
    }

    public <T> T getCastedAttribute(String name) {
        return (T)getAttribute(name, List.class);
    }

    private <T> T getAttribute(String name, Class<T> type) {
        Object value = changedAttributes.get(name);
        try {
            return (T) value;
        } catch (ClassCastException e) { // null will be OK, only other type will cause ClassCastException
            throw new FhException(String.format("'%s' attribute value is not %s. Detected type: %s.",
                    name, type.getSimpleName(), value.getClass().getSimpleName()));
        }
    }
}
