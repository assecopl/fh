package pl.fhframework.annotations;

import java.lang.annotation.*;

/**
 * Marks an auto-discovered, globally used component's XML attribute type converter.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface XMLPropertyGlobalConverter {

    Class<?> value();
}