package pl.fhframework.core.uc.url;

import java.lang.annotation.*;

/**
 * Annotation for a param which should not be exposed in URL.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface UrlParamIgnored {
}
