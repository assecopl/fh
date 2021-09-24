package pl.fhframework.core.rules.builtin;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.rules.Comment;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-09-21.
 */
@BusinessRule(categories = {"cast", "converter"})
public class CastUtils {
    @Autowired
    DateUtils dateUtils;

    @Comment("Converts value to String")
    public String toString(@Parameter(name = "from", comment = "value to convert") Object from) {
        return toStringStatic(from);
    }

    @Comment("Converts value to Short")
    public Short toShort(@Parameter(name = "from", comment = "value to convert") Object from) {
        if (from == null) {
            return null;
        }
        if (String.class.isInstance(from)) {
            return Short.valueOf((String) from);
        }
        try {
            Method method = from.getClass().getMethod("shortValue");
            return (Short) method.invoke(from);
        } catch (Exception e) {
            throw new FhException("Unsupported type for conversion", e);
        }
    }

    @Comment("Converts value to Integer")
    public Integer toInteger(@Parameter(name = "from", comment = "value to convert") Object from) {
        return toIntegerStatic(from);
    }

    @Comment("Converts value to Long")
    public Long toLong(@Parameter(name = "from", comment = "value to convert") Object from) {
        if (from == null) {
            return null;
        }
        if (String.class.isInstance(from)) {
            return Long.valueOf((String) from);
        }
        try {
            Method method = from.getClass().getMethod("longValue");
            return (Long) method.invoke(from);
        } catch (Exception e) {
            throw new FhException("Unsupported type for conversion", e);
        }
    }

    @Comment("Converts value to Float")
    public Float toFloat(@Parameter(name = "from", comment = "value to convert") Object from) {
        if (from == null) {
            return null;
        }
        if (String.class.isInstance(from)) {
            return Float.valueOf((String) from);
        }
        try {
            Method method = from.getClass().getMethod("floatValue");
            return (Float) method.invoke(from);
        } catch (Exception e) {
            throw new FhException("Unsupported type for conversion", e);
        }
    }

    @Comment("Converts value to Double")
    public Double toDouble(@Parameter(name = "from", comment = "value to convert") Object from) {
        if (from == null) {
            return null;
        }
        if (String.class.isInstance(from)) {
            return Double.valueOf((String) from);
        }
        try {
            Method method = from.getClass().getMethod("doubleValue");
            return (Double) method.invoke(from);
        } catch (Exception e) {
            throw new FhException("Unsupported type for conversion", e);
        }
    }

    @Comment("Converts value to Date")
    public LocalDate toDate(@Parameter(name = "from", comment = "value to convert") Object from) {
        return dateUtils.dateFrom(from);
    }

    @Comment("Converts value to Timestamp")
    public Date toTimestamp(@Parameter(name = "from", comment = "value to convert") Object from) {
        return dateUtils.timeFrom(from);
    }

    @Comment("Converts value to Boolean")
    public Boolean toBoolean(@Parameter(name = "from", comment = "value to convert") Object from) {
        return toBooleanStatic(from);
    }

    @Comment("Converts value to BigDecimal")
    public BigDecimal toBigDecimal(@Parameter(name = "from", comment = "value to convert") Object from) {
        if (from == null) {
            return null;
        }
        if (String.class.isInstance(from)) {
            return new BigDecimal((String) from);
        }
        return BigDecimal.valueOf(toDouble(from));
    }

    @Comment("Map collection to map, where key is value of given attribute")
    public <K, T> Map<K, T> toMap(@Parameter(name = "collection", comment = "collection to map") Collection<T> list, @Parameter(name = "attributeName", comment = "attribute key name ") String attrName) {
        if (list == null || list.isEmpty()) {
            return new HashMap<>();
        }
        Class<?> objectClass = list.iterator().next().getClass();
        Optional<Field> filed = ReflectionUtils.getPrivateField(objectClass, attrName);
        Optional<Method> getter = ReflectionUtils.findGetter(objectClass, filed.get());
        filed.get().setAccessible(true);
        return (Map<K, T>) list.stream().collect(Collectors.toMap(obj -> {
            try {
                return getter.get().invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new FhException(e);
            }
        }, Function.identity()));
    }

    @Comment("Group collection by chosen key - attribute name")
    public <K, T> Map<K, List<T>> groupBy(@Parameter(name = "collection", comment = "collection to group") Collection<T> list, @Parameter(name = "attributeName", comment = "attribute key name ") String attrName) {
        if (list == null || list.isEmpty()) {
            return new HashMap<>();
        }
        Class<?> objectClass = list.iterator().next().getClass();
        Optional<Field> filed = ReflectionUtils.getPrivateField(objectClass, attrName);
        Optional<Method> getter = ReflectionUtils.findGetter(objectClass, filed.get());
        filed.get().setAccessible(true);
        return (Map<K, List<T>>) list.stream().collect(Collectors.groupingBy(obj -> {
            try {
                return getter.get().invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new FhException(e);
            }
        }));
    }

    @Comment("Cast object to left handside var type")
    public <T> T toVarType(@Parameter(name = "object", comment = "object to cast") Object object) {
        return (T) object;
    }

    @Comment("Cast object to chosen type")
    public <T> T toType(@Parameter(name = "object", comment = "object to cast") Object object, Class<T> type) {
        return (T) object;
    }

    public static String toStringStatic(Object from) {
        if (from == null) {
            return "";
        }
        return from.toString();
    }

    public static Integer toIntegerStatic(Object from) {
        if (from == null) {
            return null;
        }
        if (String.class.isInstance(from)) {
            return Integer.valueOf((String) from);
        }
        try {
            Method method = from.getClass().getMethod("intValue");
            return (Integer) method.invoke(from);
        } catch (Exception e) {
            throw new FhException("Unsupported type for conversion", e);
        }
    }

    public static Boolean toBooleanStatic(Object from) {
        if (from == null) {
            return null;
        }
        if (String.class.isInstance(from)) {
            return "true".equalsIgnoreCase((String) from) || "1".equals(from);
        }
        return toIntegerStatic(from) != 0;
    }

    public static <T> T valueOf(String valueStr, Class<T> clazz) {
        if (!StringUtils.isNullOrEmpty(valueStr)) {
            if (Integer.class.isAssignableFrom(clazz)) {
                return (T) toIntegerStatic(valueStr);
            }
            if (Boolean.class.isAssignableFrom(clazz)) {
                return (T) toBooleanStatic(valueStr);
            }
            throw new IllegalArgumentException(String.format("No converstion to %s", clazz.getSimpleName()));
        }

        return null;
    }

    public static <T> List asList(T[] array) {
        return new ArrayList(Arrays.asList(array));
    }
}

