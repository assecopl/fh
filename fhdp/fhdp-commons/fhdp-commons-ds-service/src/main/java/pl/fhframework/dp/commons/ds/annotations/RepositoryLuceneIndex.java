package pl.fhframework.dp.commons.ds.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(value = RepositoryLuceneIndex.RepositoryLuceneIndexes.class)
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface RepositoryLuceneIndex {
	
	@Retention(RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RepositoryLuceneIndexes {
		RepositoryLuceneIndex[] value() default {};
	}	
	
	public String name() default "";
	public String tag() default "";
	public String[] types() default {};
	public String version() default "";
	public String indexinglane() default "";
	public long indexinglaneinterval() default 5;
	public String[] paths() default {};
	public String indexType() default "";
	public String nrt() default "";
	public String indexOriginalTerm() default "";	
	public String codec() default "";	
	public String analyserClass() default "";	
}

