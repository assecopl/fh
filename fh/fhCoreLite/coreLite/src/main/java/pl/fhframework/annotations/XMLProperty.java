package pl.fhframework.annotations;

import pl.fhframework.model.forms.attribute.IComponentAttributeTypeConverter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface XMLProperty {

    public interface NoConverter extends IComponentAttributeTypeConverter {
    }

    String value() default "";

    String defaultValue() default "";

    boolean editable() default true;

    boolean required() default false;

    String[] aliases() default {};

    /**
     * If should this property be skipped during compilation
     */
    boolean skipCompiler() default false;

    Class<? extends IComponentAttributeTypeConverter> converter() default NoConverter.class;
}