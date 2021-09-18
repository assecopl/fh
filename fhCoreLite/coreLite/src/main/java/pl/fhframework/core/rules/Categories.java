package pl.fhframework.core.rules;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Categories {
    // categories of method
    String[] value();
}
