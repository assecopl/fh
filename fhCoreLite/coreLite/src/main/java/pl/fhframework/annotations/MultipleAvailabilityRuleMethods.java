package pl.fhframework.annotations;

import java.lang.annotation.*;

/**
 * Created by Gabriel on 01.06.2016.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface MultipleAvailabilityRuleMethods {
    AvailabilityRuleMethod[] value();
}
