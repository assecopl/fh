package pl.fhframework.core.maps.features.json;

import java.lang.annotation.*;

/**
 * Created by pawel.ruta on 2019-01-15.
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureClassDiscriminator {
    String classDiscriminatorKey() default "";
    String featureClass() default "";
}
