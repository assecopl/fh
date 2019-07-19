package pl.fhframework.core.documented;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that allows to document parameters of constructor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface DocumentedParameter {

    /**
     * Name of class of a documented parameter
     * @return
     */
    String className() default "";

    /**
     * Name of a documented parameter
     * @return
     */
    String parameterName() default "";

    /**
     * Description of a documented parameter
     * @return
     */
    String description() default "";
}
