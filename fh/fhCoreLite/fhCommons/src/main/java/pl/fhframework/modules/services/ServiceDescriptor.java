package pl.fhframework.modules.services;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Created by pawel.ruta on 2018-10-19.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceDescriptor {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    Class<?> bean() default void.class;
}
