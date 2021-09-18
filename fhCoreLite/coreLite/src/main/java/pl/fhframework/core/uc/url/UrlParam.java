package pl.fhframework.core.uc.url;

import java.lang.annotation.*;

/**
 * Annotation for a param of an use case exposed in URL.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface UrlParam {

    /**
     * Position of this parameter in URL's path. Makes this parameter positional.
     */
    int position() default -1;

    /**
     * Name of this parameter in URL's query. Makes this parameter named.
     */
    String name() default "";

    /**
     * If this parameter is optional and may be ommited in URL. Default: false.
     */
    boolean optional() default false;
}
