package pl.fhframework.core.security.annotations;

import pl.fhframework.core.services.FhService;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be placed on {@link pl.fhframework.core.uc.UseCase} use case class,
 * {@link pl.fhframework.annotations.Action} action method in use case,
 * {@link pl.fhframework.core.rules.BusinessRule} rule class, method in rule class,
 * {@link FhService} service class or method in service class.
 */
@Documented
@Repeatable(SystemFunctions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SystemFunction {

    String value();

}
