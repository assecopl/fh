package pl.fhframework.annotations;

import java.lang.annotation.*;

/**
 * List of overriden annotations for a XML property field inherited from a superclass.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface OverridenPropertyAnnotationsList {

    /**
     * List of overriden annotations
     */
    OverridenPropertyAnnotations[] value();
}
