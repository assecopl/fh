package pl.fhframework.core.uc.url;

import java.lang.annotation.*;

/**
 * Annotation for a param of an use case which wraps URL params inside.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface UrlParamWrapper {

    /**
     * Prefix for embedded named parameters.
     */
    String namePrefix() default "";

    /**
     * If using names for embedded parameters is the default method.
     */
    boolean useNames() default true;
}
