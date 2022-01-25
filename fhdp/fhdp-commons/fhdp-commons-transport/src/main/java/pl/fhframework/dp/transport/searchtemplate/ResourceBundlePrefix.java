package pl.fhframework.dp.transport.searchtemplate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adnotacja do opisania klasy DTO,
 * dla której zdefiniowano etykiety w ResourceBundle
 *
 * Adnotacja oznacza wspólny przedrostek dla etykiet dla danego DTO,
 * najczęściej jest pełną nazwą klasy (pakiet i nazwa klasy)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResourceBundlePrefix {
    String value() default "";
}
