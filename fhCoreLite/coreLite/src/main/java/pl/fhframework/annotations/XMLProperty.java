package pl.fhframework.annotations;

import pl.fhframework.model.forms.attribute.IComponentAttributeTypeConverter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface XMLProperty {

    interface NoConverter extends IComponentAttributeTypeConverter {
    }

    class Defaults {
        private static final XMLProperty defaults = null;

        public static XMLProperty getOrDefaults(Class<?> componentClass, Field field) {
            if (field.isAnnotationPresent(XMLProperty.class)) {
                // try to find overriden in component class
                while (componentClass != Object.class) {
                    Optional<OverridenPropertyAnnotations> overridenAnnotations = Arrays.stream(getOverridenPropertyAnnotations(componentClass))
                            .filter(a -> a.property().equals(field.getName()))
                            .findFirst();
                    if (overridenAnnotations.isPresent() && overridenAnnotations.get().xmlProperty().length == 1) {
                        return overridenAnnotations.get().xmlProperty() [0];
                    }
                    // find in superclass
                    componentClass = componentClass.getSuperclass();
                }
                // get original from field
                return field.getAnnotation(XMLProperty.class);
            } else {
                return defaults;
            }
        }

        private static OverridenPropertyAnnotations[] getOverridenPropertyAnnotations(Class<?> componentClass) {
            if (componentClass.isAnnotationPresent(OverridenPropertyAnnotationsList.class)) {
                return componentClass.getAnnotation(OverridenPropertyAnnotationsList.class).value();
            } else if (componentClass.isAnnotationPresent(OverridenPropertyAnnotations.class)) {
                return new OverridenPropertyAnnotations[] { componentClass.getAnnotation(OverridenPropertyAnnotations.class) };
            } else {
                return new OverridenPropertyAnnotations[0];
            }
        }
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
