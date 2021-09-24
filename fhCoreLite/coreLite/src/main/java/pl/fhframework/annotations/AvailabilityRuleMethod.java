package pl.fhframework.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Gabriel on 2015-12-09.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
@Repeatable(MultipleAvailabilityRuleMethods.class)
public @interface AvailabilityRuleMethod {
    String value();
}
