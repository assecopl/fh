package pl.fhframework.compiler.core.uc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares use case dependecies which are expected to be fullfilled by the cloud or an external module.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseCaseDependencies {

    /**
     * Dependencies
     */
    UseCaseDependency[] value();

}