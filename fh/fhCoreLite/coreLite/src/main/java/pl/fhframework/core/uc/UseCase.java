package pl.fhframework.core.uc;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public @interface UseCase {

    // whether usecase is dynamic
    boolean modifiable() default  false;

    // the suggested component name, if any
    String value() default "";
}