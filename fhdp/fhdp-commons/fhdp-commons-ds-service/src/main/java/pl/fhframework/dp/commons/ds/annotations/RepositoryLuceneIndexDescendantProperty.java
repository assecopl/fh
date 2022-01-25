package pl.fhframework.dp.commons.ds.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Repeatable(value = RepositoryLuceneIndexDescendantProperty.RepositoryLuceneIndexDescendantProperties.class)
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface RepositoryLuceneIndexDescendantProperty {
	
	@Retention(RUNTIME)
	@Target(ElementType.TYPE)
	public @interface RepositoryLuceneIndexDescendantProperties {
		RepositoryLuceneIndexDescendantProperty[] value() default {};
	}	
	
	public long weight() default -1;
	public boolean propertyIndex() default true;
	/*
	 * If the property is to be used in order by clause to perform sorting then this should be set to true. This should be set to true only if the property is to be used to perform sorting as it increases the index size. Example
		//element(*, app:Asset)[jcr:contains(type, ‘image’)] order by @size
		//element(*, app:Asset)[jcr:contains(type, ‘image’)] order by jcr:content/@jcr:lastModified
	 */
	public String ordered() default "";
	/*
	 * Set this to true if the property is used as part of contains. Example
		//element(*, app:Asset)[jcr:contains(type, ‘image’)]
		//element(*, app:Asset)[jcr:contains(jcr:content/metadata/@format, ‘image’)]
	 */
	public String analyzed() default "";
	public String regexp() default "";
	public String boost() default "";
	public String[] indexes() default {};
	/*
	 * JCR Property type. Can be one of Date, Boolean, Double , String or Long. Mostly inferred from the indexed value. However in some cases where same property type is not used consistently across various nodes then it would recommended to specify the type explicitly.
	 */
	public String type() default "";
	public String name() default "";
	public String function() default "";
	public String path() default "";
}

