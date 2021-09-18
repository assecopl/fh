package pl.fhframework.core.documented;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation that allows to have documentation of class inside FH Application.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface DocumentedClass {

    /**
     * Description of a documented class
     * @return
     */
    String description() default "";
}
