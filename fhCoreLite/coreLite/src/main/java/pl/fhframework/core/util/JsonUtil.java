package pl.fhframework.core.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static ObjectMapper mapper;

    /**
     * Creates new, default configured ObjectMapper instance;
     */
    private static ObjectMapper createOM() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
            mapper.enable(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS);
            mapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
            mapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.enable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
            mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        return mapper;
    }

    /**
     * Read value from string using default mapper
     */
    public static <T> T readValue(String x, Class<T> c) {
        ObjectMapper om = createOM();
        try {
            return om.readValue(x, c);
        } catch (Exception ex) {
            FhLogger.error(ex);
            return null;
        }
    }

    public static <T> T readValue(Map x, Class<T> c) {
        ObjectMapper om = createOM();
        try {
            return om.convertValue(x, c);
        } catch (Exception ex) {
            FhLogger.error(ex);
            return null;
        }
    }

    public static <T> List<T> readValues(String x, TypeReference<List<T>> typeRef) {
        ObjectMapper om = createOM();
        JavaType typeT = om.getTypeFactory().constructType(typeRef);
        try {
            return om.readValue(x, typeT);
        } catch (Exception ex) {
            FhLogger.error(ex);
            return null;
        }
    }

    public static <T> String writeValue(T obj) {
        if (obj == null) {
            return "null";
        }
        ObjectMapper om = createOM();
        try {
            return om.writeValueAsString(obj);
        } catch (Exception ex) {
            FhLogger.error(ex);
            return "null";
        }
    }

    /**
     * If input isn't String it converts value to JSON string otherwise returns this String
     * @param obj object
     * @param <T>  input
     * @return
     */
    public static <T> String stringifyUnlessString(T obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        ObjectMapper om = createOM();
        try {
            return om.writeValueAsString(obj);
        } catch (Exception ex) {
            FhLogger.error(ex);
            return "null";
        }
    }

    public static String getPropertyValueAsString(JsonNode jsonNode, String propName) {
        if (jsonNode == null) {
            return null;
        }
        JsonNode propJson = jsonNode.get(propName);
        return propJson.textValue();
    }

    public static String[] convertObjects(Object[] objects) {
        String[] jsons = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            try {
                jsons[i] = createOM().writeValueAsString(objects[i]);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error marshalling object", e);
            }
        }
        return jsons;
    }

    public static Object[] convertObjects(String[] jsons, Class[] types) {
        Object[] objects = new Object[jsons.length];
        for (int i = 0; i < jsons.length; i++) {
            try {
                objects[i] = createOM().readValue(jsons[i], types[i]);
            } catch (IOException e) {
                throw new RuntimeException("Error unmarshalling object", e);
            }
        }
        return objects;
    }

    public static ObjectNode readObjectNode(Object value) {
        return createOM().valueToTree(value);
    }

    public static TreeNode readNode(JsonParser parser) {
        try {
            return createOM().readTree(parser);
        } catch (IOException e) {
            throw new FhException(e);
        }
    }

    public static <T> T readTree(TreeNode treeNode, Class<T> valueType) {
        try {
            return createOM().treeToValue(treeNode, valueType);
        } catch (IOException e) {
            throw new FhException(e);
        }
    }
}
