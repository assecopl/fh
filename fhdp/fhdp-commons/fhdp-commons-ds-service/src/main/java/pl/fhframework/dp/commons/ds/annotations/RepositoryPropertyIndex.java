package pl.fhframework.dp.commons.ds.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface RepositoryPropertyIndex {
	public String name() default "";
	public String subtype() default "";
	public String tag() default "";
	public boolean unique() default false;
	public String version() default "";

}
