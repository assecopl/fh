package pl.fhframework.dp.commons.fh.utils.objects.init;

import pl.fhframework.core.logging.FhLogger;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Performs automatic initialization of non-null fields of simple POJO class, ex. Document
 */
public class PojoInitializer {

    //if recursion reaches max recursion depth method will throw an exception to avoid stack overflow
    private static final int MAX_RECURSION_DEPTH = 10;

    public static void initializePojo(Object object, PojoInitializerConfig config) {
        try {
            recursiveInitialize(object, config.getSkipByType(), config.getSkipByLocation(), "", 0);
        } catch (InstantiationException | IllegalAccessException e) {
            FhLogger.warn("Could not initialize {} {}", e.getMessage(), e);
        }
    }

    private static void recursiveInitialize(Object object, Set<String> skipByType, Set<String> skipByLocation, String parentFieldLocation, int recursionDepth) throws IllegalAccessException, InstantiationException {
        if (recursionDepth >= MAX_RECURSION_DEPTH)
            throw new RuntimeException("Object initialization has reached max recursion depth +" + MAX_RECURSION_DEPTH);
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            Class<?> fieldClass = field.getType();
            String fieldLocation = parentFieldLocation + "." + fieldName;
            if (fieldClass.isPrimitive() || fieldClass.isEnum() || skipByType.contains(fieldClass.getCanonicalName()) || skipByLocation.contains(fieldLocation)) {
                continue;
            }
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            String loggerMsg = fieldLocation + " of type:" + fieldClass.getCanonicalName();
            Object fieldValue = field.get(object);
            if (fieldValue == null) {
                FhLogger.debug("Initializing {}", loggerMsg);
                field.set(object, fieldClass.newInstance());
            } else {
                FhLogger.debug("Field {} already initialized: ", loggerMsg);
            }
            fieldValue = field.get(object);
            field.setAccessible(isAccessible);
            recursiveInitialize(fieldValue, skipByType, skipByLocation, fieldLocation, recursionDepth + 1);
        }
    }
}
