package pl.fhframework.dp.commons.ds.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(value = RepositoryDescendantPropertyIndex.RepositoryDescendantPropertyIndexes.class)
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface RepositoryDescendantPropertyIndex {
	
	@Retention(RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RepositoryDescendantPropertyIndexes {
		RepositoryDescendantPropertyIndex[] value() default {};
	}	

	public String name() default "";
	public String subtype() default "";
	public String tag() default "";
	public String propertyName() default "";
}

