package pl.fhframework.core.security.annotations;

import java.lang.annotation.*;

/**
 * @author Tomasz.Kozlowski (created on 21.09.2018)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface SystemRoles {

    SystemRole[] value();

}
