package pl.fhframework.dp.transport.searchtemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adnotacja do opisania pola klasy DTO
 * dla której zdefiniowano etykiety w ResourceBundle
 *
 * Adnotacja oznacza pole dla którego nie zdefiniowano etykiety w ResourceBundle
 * i wyklucza to pole z parsowania (zwykle stosowane np. dla pól typu id lub innych
 * technicznych, nie do użytku przez użytkownika)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotInResourceBundle {
    String value() default "";
}