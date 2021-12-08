package pl.fhframework.dp.commons.ds.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(value = RepositoryLuceneIndexAggregate.RepositoryLuceneIndexAggregates.class)
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface RepositoryLuceneIndexAggregate {
	
	@Retention(RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RepositoryLuceneIndexAggregates {
		RepositoryLuceneIndexAggregate[] value() default {};
	}	
	
	public String path() default "";
	public boolean relative() default false;
	public String primaryType() default "";
}

