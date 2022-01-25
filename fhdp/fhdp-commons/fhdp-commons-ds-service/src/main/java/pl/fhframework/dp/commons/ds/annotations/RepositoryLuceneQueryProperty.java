package pl.fhframework.dp.commons.ds.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(ElementType.FIELD)
public @interface RepositoryLuceneQueryProperty {
	public String property() default "";
	public String dateFormat() default "";
	public String inputDateFormat() default "";
	public String inputDateTimeFormat() default "";
	public ConditionTypeEnum condition() default ConditionTypeEnum.EQUALS;
}
