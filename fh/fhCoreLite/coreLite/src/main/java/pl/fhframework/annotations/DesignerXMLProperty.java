package pl.fhframework.annotations;

import pl.fhframework.core.designer.IDesignerAttributeSupport;
import pl.fhframework.model.forms.designer.DefaultDesignerFixedValuesProvider;
import pl.fhframework.model.forms.designer.DefaultDesignerPreviewProvider;
import pl.fhframework.model.forms.designer.IDesignerFixedValuesProvider;
import pl.fhframework.model.forms.designer.IDesignerPreviewProvider;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DesignerXMLProperty {

    class Defaults {

        @DesignerXMLProperty
        private static int field;
        private static DesignerXMLProperty defaults;
        static {
            try {
                defaults = Defaults.class.getDeclaredField("field").getAnnotation(DesignerXMLProperty.class);
            } catch (NoSuchFieldException neverHappens) {}
        }

        public static DesignerXMLProperty getDefaults() {
            return defaults;
        }

        public static DesignerXMLProperty getOrDefaults(Class<?> componentClass, Field field) {
            if (field.isAnnotationPresent(DesignerXMLProperty.class)) {
                // try to find overriden in component class
                while (componentClass != Object.class) {
                    Optional<OverridenPropertyAnnotations> overridenAnnotations = Arrays.stream(getOverridenPropertyAnnotations(componentClass))
                            .filter(a -> a.property().equals(field.getName()))
                            .findFirst();
                    if (overridenAnnotations.isPresent() && overridenAnnotations.get().designerXmlProperty().length == 1) {
                        return overridenAnnotations.get().designerXmlProperty()[0];
                    }
                    // find in superclass
                    componentClass = componentClass.getSuperclass();
                }
                // get original from field
                return field.getAnnotation(DesignerXMLProperty.class);
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

    class DefaultNOPAttributeSupport implements IDesignerAttributeSupport {}

    /**
     * Defines if this property is commonly used and should be
     * displayed together with required properties
     */
    boolean commonUse() default false;

    /**
     * Defines if this property should be skipped by designer
     * properties window, for example - size should not be set manually in window, but calculated
     * automatically by resize action
     */
    boolean readOnlyInDesigner() default false;

    /**
     * Defines priority for attribute, on which position on a list attribute, it will be displayed.
     * The highest value will put attribute on top of a list, the lowest value at the bottom of a list.
     * Warning!!! This priority works within scope of functionalArea.
     */
    int priority() default 0;

    /**
     * Defines what is the functional area of given property
     */
    PropertyFunctionalArea functionalArea() default PropertyFunctionalArea.SPECIFIC;

    /**
     * Defines types expected to be returned by the binding in this property
     */
    Class[] allowedTypes() default Object.class;

    /**
     * Defines if only bindings are alowed
     */
    boolean bindingOnly() default false;

    /**
     * Skip this property in designer
     */
    boolean skip() default false;

    /**
     * Defines a provider of binding values in preview mode
     */
    Class<? extends IDesignerPreviewProvider> previewValueProvider() default DefaultDesignerPreviewProvider.class;

    Class<? extends IDesignerFixedValuesProvider> fixedValuesProvider() default DefaultDesignerFixedValuesProvider.class;

    Class<? extends IDesignerAttributeSupport> support() default DefaultNOPAttributeSupport.class;

    enum PropertyFunctionalArea {

        CONTENT("Content"),
        LOOK_AND_STYLE("Look & Style"),
        BEHAVIOR("Behavior"),
        SPECIFIC("Specific");

        private final String text;

        PropertyFunctionalArea(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}