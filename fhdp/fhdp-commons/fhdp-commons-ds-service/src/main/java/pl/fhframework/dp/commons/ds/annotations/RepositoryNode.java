package pl.fhframework.dp.commons.ds.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;


@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface RepositoryNode {
	public String type() default "";
	public String ns() default "";
	public String base() default "";
	public boolean anyProperty() default true;
}
