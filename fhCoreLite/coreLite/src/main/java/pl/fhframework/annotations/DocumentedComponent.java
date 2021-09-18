package pl.fhframework.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by szkiladza on 22.11.2016.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DocumentedComponent {
    String value() default "";
    String icon() default "";
    String[] ignoreFields() default "";
    String category() default "";
}
