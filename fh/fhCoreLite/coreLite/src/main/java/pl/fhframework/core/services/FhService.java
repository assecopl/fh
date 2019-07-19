package pl.fhframework.core.services;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Service
public @interface FhService {

    // whether service is dynamic
    boolean modifiable() default  false;

    // the suggested bean name, default to simple class name
    String value() default "";

    // the suggested fh service group name, default to value()
    String groupName() default "";

    String[] categories() default {};

    String description() default "";
}