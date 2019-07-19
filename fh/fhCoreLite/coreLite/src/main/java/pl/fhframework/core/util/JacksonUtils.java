package pl.fhframework.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;

import java.util.List;

/**
 * Created by pawel.ruta on 2018-04-23.
 */
@Service
public class JacksonUtils {
    public static final String UTIL_NAME = "__jacksonUtils";

    public static final String TEMPLATE_NAME = "__restTemplate";

    public static final String ATTR_NAME = "fh$outputjson";

    @Autowired
    private ObjectMapper objectMapper;

    public <T extends JsonNode> T valueToTree(Object object) {
        return objectMapper.valueToTree(object);
    }

    public <T> String writeValueAsString(T object) {
        if (object == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception ex) {
            throw new FhException(ex);
        }
    }

    public <T> String writeValueAsStringPretty(T object) {
        if (object == null) {
            return "null";
        }
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception ex) {
            throw new FhException(ex);
        }
    }

    /**
     * If input isn't String it converts value to JSON string otherwise returns this String
     *
     * @param object object
     * @param <T>    input
     * @return
     */
    public <T> String writeValueAsText(T object) {
        if (object == null) {
            return "null";
        }

        return valueToTree(object).asText();
    }

    /**
     * If input isn't String it converts value to JSON string otherwise returns this String
     * @param obj object
     * @param <T>  input
     * @return
     */
    public <T> String stringifyUnlessString(T obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            return writeValueAsString(obj);
        } catch (Exception ex) {
            throw new FhException(ex);
        }
    }

    /**
     * Read value from string using default mapper
     */
    public <T> T readValueFromString(String json, Class<T> c) {
        try {
            return objectMapper.readValue(json, c);
        } catch (Exception ex) {
            FhLogger.error(ex);
            return null;
        }
    }

    public <T> List<T> readValuesFromString(String json, TypeReference<List<T>> typeRef) {
        JavaType typeT = objectMapper.getTypeFactory().constructType(typeRef);
        try {
            return objectMapper.readValue(json, typeT);
        } catch (Exception ex) {
            FhLogger.error(ex);
            return null;
        }
    }

    public static boolean isHttpRequest() {
        return RequestContextHolder.getRequestAttributes() != null;
    }
}
