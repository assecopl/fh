package pl.fhframework.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by szkiladza on 23.11.2016.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface DocumentedComponentAttribute {

    String value() default "";

    String defaultValue() default "";

    Class<?> type() default EMPTY.class;

    boolean boundable() default false;

    boolean canReadNested() default false;

    class EMPTY {
    }
}
