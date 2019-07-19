package pl.fhframework.core.rules;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Comment {
    // comment of method
    String value();
}
