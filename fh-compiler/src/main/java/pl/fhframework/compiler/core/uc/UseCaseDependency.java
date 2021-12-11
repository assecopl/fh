package pl.fhframework.compiler.core.uc;

import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.IUseCaseNoCallback;

import java.lang.annotation.*;

/**
 * Declares use case dependecy which is expected to be fullfilled by the cloud or an external module.
 * Must specify use case interface or use case full name (but not both).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UseCaseDependencies.class)
public @interface UseCaseDependency {

    /**
     * Marker class which is default value (no selection) for useCaseInterface property.
     */
    public final class NULL implements IUseCase<IUseCaseNoCallback> {

        private NULL() {
        }
    }

    /**
     * Required use case interface class.
     */
    Class<? extends IUseCase<?>> useCaseInterface() default NULL.class;

    /**
     * Required use case or interface full class name.
     */
    String useCaseFullName() default "";

}