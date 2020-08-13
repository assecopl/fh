package pl.fhframework.annotations.composite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by krzysztof.kobylarek on 2017-01-12.
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FireEvent {
    String[] name() ;
}
