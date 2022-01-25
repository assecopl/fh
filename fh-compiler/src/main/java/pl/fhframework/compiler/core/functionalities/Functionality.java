package pl.fhframework.compiler.core.functionalities;

import java.lang.annotation.*;

/**
 * Annotations which allows mark functionality and manage it inside module info file
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface Functionality {
    /**
     * Functionality name
     * @return Functionality name
     */
    String value();
}
