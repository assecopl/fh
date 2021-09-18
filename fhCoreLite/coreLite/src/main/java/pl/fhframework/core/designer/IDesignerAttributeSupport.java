package pl.fhframework.core.designer;

import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.validation.FieldValidationResult;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Interface of an attribute designer supporting class.
 * Objects of this class are meant to be cached and must be stateless.
 * @param <T> attribute value type
 */
public interface IDesignerAttributeSupport<T> {

    /**
     * Does a custom validation of attribute's value
     *
     * @param form owning form
     * @param component owning component
     * @param oldValue old value
     * @param newValue new value
     * @return optional validation message
     */
    public default Optional<FieldValidationResult> validate(Form<?> form, Component component, T oldValue, T newValue, Type finalType) {
        return Optional.empty();
    }

    /**
     * Called after changing attribute value in designer
     *
     * @param form owning form
     * @param component owning component
     * @param oldValue old value
     * @param newValue new value
     */
    public default void onChange(Form<?> form, Component component, T oldValue, T newValue) {
    }

    /**
     * Checks if attribute should be skipped on write
     * @param form owning form
     * @param component owning component
     * @param value attribute value
     * @return if attribute should be skipped on write
     */
    public default boolean skipOnWrite(Form<?> form, Component component, T value) {
        return false;
    }
}
