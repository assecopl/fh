package pl.fhframework.annotations;

import java.lang.annotation.*;

/**
 * Overriden annotations for a XML property field inherited from a superclass.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(OverridenPropertyAnnotationsList.class)
public @interface OverridenPropertyAnnotations {

    /**
     * Field name
     */
    String property();

    /**
     * One or none overriden @DesignerXMLProperty
     */
    DesignerXMLProperty[] designerXmlProperty();
}
