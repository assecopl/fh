package pl.fhframework.dp.commons.utils.conversion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.fhframework.dp.commons.utils.json.FhdpObjectMapper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 18.09.2019
 */
@Slf4j
public class BeanConversionUtil {

    public static <T> T mapObject(Object source, boolean withoutNulls, Class<T> dtoClazz) {
        FhdpObjectMapper mapper = new FhdpObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        if(withoutNulls) {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        try {
            String json = mapper.writeValueAsString(source);
            return mapper.readValue(json, dtoClazz);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static <T> T getFromJson(String json, Class<T> mappedClass) {
        T mappedCase = null;
        ObjectMapper mapper = new FhdpObjectMapper();
        try {
            mappedCase = mapper.readValue(json, mappedClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappedCase;
    }

    public  static <T> T getFromJson(String json, TypeReference<T> typeRef) {
        T mappedCase = null;
        ObjectMapper mapper = new FhdpObjectMapper();
        try {
            mappedCase = mapper.readValue(json, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappedCase;
    }

    public static String toJson(Object bean) {
        ObjectMapper mapper = new FhdpObjectMapper();
        try {
            return mapper.writeValueAsString(bean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //debug and logging utility to print formatted Json
    public static String toPrettyJson(Object bean) {
        ObjectMapper mapper = new FhdpObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getSenderId(String messageSender, Object msg) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        String fullSenderIdPath = messageSender.replace("/", ".").substring(1);
        String decName = StringUtils.substringBefore(fullSenderIdPath, ".");
        String senderIdPath = fullSenderIdPath.substring(decName.length());
        senderIdPath = toLowerCaseAfterDot(senderIdPath);
        String ret = null;
        try {
            ret = (String) PropertyUtils.getNestedProperty(msg, senderIdPath.substring(1));
        } catch (Exception ex) {
            log.error("Nested property error", ex);
        }
        return ret;
    }

    private static String toLowerCaseAfterDot(String xpath) {
        char[] xpathArray = xpath.toCharArray();
        for(int i = 1; i < xpathArray.length; i++)
        {
            if(xpathArray[i-1] == '.')
                xpathArray[i] = Character.toLowerCase(xpathArray[i]);
        }
        return new String(xpathArray);
    }
}
