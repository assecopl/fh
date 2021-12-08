package pl.fhframework.dp.commons.ds.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(value = RepositoryNodeType.RepositoryNodeTypes.class)
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface RepositoryNodeType {
	
	@Retention(RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RepositoryNodeTypes {
		RepositoryNodeType[] value() default {};
	}	
	
	public Class<?> objectClass();
	public String type();

}

