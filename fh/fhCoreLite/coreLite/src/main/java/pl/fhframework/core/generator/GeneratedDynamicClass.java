package pl.fhframework.core.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for generated dynamic classes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface GeneratedDynamicClass {

    String value();
}
