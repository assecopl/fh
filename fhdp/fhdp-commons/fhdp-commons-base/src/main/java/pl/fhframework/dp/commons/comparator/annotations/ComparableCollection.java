package pl.fhframework.dp.commons.comparator.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented

public @interface ComparableCollection {

	String Xpath() default "";
}
