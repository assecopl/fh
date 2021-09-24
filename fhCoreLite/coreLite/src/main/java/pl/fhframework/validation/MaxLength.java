package pl.fhframework.validation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD})
public @interface MaxLength {
    int value() default 2147483647;
}
