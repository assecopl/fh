package pl.fhframework.annotations.composite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by krzysztof.kobylarek on 2016-12-22.
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface  Composite {
    String template();
    String[] registeredEvents() default "";
    Class<?> model() default EMPTY_MODEL.class;
    class EMPTY_MODEL {}
}