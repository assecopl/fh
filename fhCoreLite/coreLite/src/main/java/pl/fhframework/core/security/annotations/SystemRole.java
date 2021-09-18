package pl.fhframework.core.security.annotations;

import pl.fhframework.core.services.FhService;

import java.lang.annotation.*;

/**
 * Annotation that can be placed on {@link pl.fhframework.core.uc.UseCase} use case class,
 * {@link pl.fhframework.annotations.Action} action method in use case,
 * {@link pl.fhframework.core.rules.BusinessRule} rule class, method in rule class,
 * {@link FhService} service class or method in service class.
 *
 * @author Tomasz.Kozlowski (created on 21.09.2018)
 */
@Documented
@Repeatable(SystemRoles.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SystemRole {

    String value();

}
