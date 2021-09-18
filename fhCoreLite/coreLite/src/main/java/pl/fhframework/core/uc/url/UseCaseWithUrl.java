package pl.fhframework.core.uc.url;

import java.lang.annotation.*;

/**
 * Annotation for an use cases that can be started by entering special URL in the browser.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface UseCaseWithUrl {

    public static final String DEFAULT_ALIAS = "";

    /**
     * Use case name in URL. Defaults to simple use case class name.
     */
    String alias() default DEFAULT_ALIAS;

    /**
     * Use case URL adapter class. This adapter must be a stateless as it is meant to be cached.
     */
    Class<? extends IUseCaseUrlAdapter<?>> adapterClass() default AnnotatedParamsUseCaseUrlAdapter.class;
}
