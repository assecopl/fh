package pl.fhframework.annotations;

import java.lang.annotation.*;

/**
 * Injects a component to a field on a form.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface InjectComponent {

    /**
     * Injected component id, defaults to field name.
     */
    String value() default "";
}
