package pl.fhframework.dp.commons.base.comparator.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface ComparableClass {

	String Xpath() default "";
}
