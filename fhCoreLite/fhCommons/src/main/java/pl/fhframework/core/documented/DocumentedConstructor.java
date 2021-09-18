package pl.fhframework.core.documented;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that allows to document constructor inside FH Application.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.CONSTRUCTOR)
public @interface DocumentedConstructor {

     /**
      * Description of a documented constructor
      * @return
      */
     String description() default "";

//     String[] params() default "";
     /**
      * Parameters of a documented constructor
      * @return
      */
     DocumentedParameter[] parameters() default {};
}
