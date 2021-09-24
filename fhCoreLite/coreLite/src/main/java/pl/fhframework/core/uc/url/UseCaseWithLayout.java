package pl.fhframework.core.uc.url;

import java.lang.annotation.*;

/**
 * Annotation for an use cases that can be started by entering special URL in the browser.
 * Not working with disigner.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface UseCaseWithLayout {
    /**
     * Use case name in URL. Defaults to simple use case class name.
     */
    String layout() default "standard";


}
