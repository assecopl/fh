package pl.fhframework.dp.commons.ds.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface RepositorySubtype {
	public String type() default "";
	public String ns() default "";
	public String base() default "";
	public boolean oncreate() default true;
}
