package pl.fhframework.core.util;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import pl.fhframework.ReflectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pawel.ruta on 2018-11-15.
 */
public class DynamicTypeDeserializerUtils {
    private static Map<Class<?>, Map<String, Class<?>>> subTypes = new ConcurrentHashMap<>();

    public static <T> Class<? extends T> getSubType(Class<T> superType, String subType) {
        return (Class<? extends T>) subTypes.computeIfAbsent(superType, key -> {
            Map<String, Class<?>> st = new HashMap<>();
            List<Class<? extends T>> classes = ReflectionUtils.getAnnotatedClasses(JsonSubTypes.class, superType);
            classes.forEach(clazz -> {
                for (JsonSubTypes.Type type : clazz.getAnnotation(JsonSubTypes.class).value()) {
                    st.put(type.name(), type.value());
                }
            });
            return st;
        }).get(subType);
    }
}
