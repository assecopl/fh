package pl.fhframework.core.rules;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.util.List;

/**
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public @interface BusinessRule {

    // whether rule is dynamic
    boolean modifiable() default  false;

    // the suggested component name, if any
    String value() default "";

    String[] categories() default {};

    String description() default "";
}