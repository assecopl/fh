package pl.fhframework.validation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD})
public @interface MinLength {
    int value() default 0;
}
