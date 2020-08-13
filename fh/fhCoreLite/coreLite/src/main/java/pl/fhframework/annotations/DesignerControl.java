package pl.fhframework.annotations;

import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.FormElement;

import java.lang.annotation.*;
import java.util.Objects;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DesignerControl {
    int defaultWidth() default 12;

    boolean canStackPropertiesForm() default false;

    boolean forceShowInElementTree() default false;

    class Utils {
        public static DesignerControl getDesignerControlProperties(Class<? extends Component> clazz) {
            Objects.requireNonNull(clazz);

            Class<?> inspectedClass = clazz;
            while (FormElement.class.isAssignableFrom(inspectedClass) && !inspectedClass.isAnnotationPresent(DesignerControl.class)) {
                inspectedClass = inspectedClass.getSuperclass();
            }

            return inspectedClass.getAnnotation(DesignerControl.class);
        }
    }
}
