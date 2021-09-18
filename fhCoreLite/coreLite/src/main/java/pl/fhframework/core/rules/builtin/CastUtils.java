package pl.fhframework.core.rules.builtin;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.FhException;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.rules.Comment;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

