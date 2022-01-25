package pl.fhframework.dp.transport.searchtemplate;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adnotacja do opisania pola klasy DTO
 * którego wartość jest pobierana ze słownika
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DictName {
    //np 1804
    String dictName() default "";
    //np kod lub opis
    String dictFieldName() default "";
}




