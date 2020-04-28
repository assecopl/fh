package pl.fhframework.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Control {
    /**
     * List of valid parents. Includes subclasses of listed classes.
     */
    Class<?>[] parents() default {Object.class};

    /**
     * List of invalid parents. Includes subclasses of listed classes.
     */
    Class<?>[] invalidParents() default {Void.class};

    /**
     * This flag informs programmers if control component can be used in designed mode for adding
     * for example to Form. Default is false so it won't be possible to use this class in designer.
     */
    boolean canBeDesigned() default false;

    boolean override() default false;
}
