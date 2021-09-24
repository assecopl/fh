package pl.fhframework.core.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Annotation for generated dynamic classes' elements (fields and methods). When used on a type concerns all elements declared in this type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
public @interface ModelElement {

    public static class Util {

        public static ModelElementType getType(Method method) {
            return getType(method.getAnnotation(ModelElement.class));
        }

        public static ModelElementType getType(Class<?> clazz) {
            return getType(clazz.getAnnotation(ModelElement.class));
        }

        public static ModelElementType getType(Field field) {
            return getType(field.getAnnotation(ModelElement.class));
        }

        private static ModelElementType getType(ModelElement annot) {
            return annot != null ? annot.type() : null;
        }
    }

    ModelElementType type() default ModelElementType.OTHER;
}
