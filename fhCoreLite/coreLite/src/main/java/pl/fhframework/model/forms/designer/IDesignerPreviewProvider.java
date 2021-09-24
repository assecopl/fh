package pl.fhframework.model.forms.designer;

import pl.fhframework.binding.DesignerModelBinding;
import pl.fhframework.model.forms.Component;

import java.lang.reflect.Field;

/**
 * Interface of designer's preview binding value provider.
 * Objects of this class are meant to be cached and must be stateless.
 */
public interface IDesignerPreviewProvider {

    /**
     * Returns preview value
     * @param component component
     * @param field field with XMLProperty annotation
     * @param modelBinding model binding
     * @param bindingExpression binding expression
     * @param bindingStaticValue static value (value of a static binding, null if not a static)
     * @param propertyDefaultValue default value (defaultValue property of XMLProperty)
     * @return value for preview
     */
    public Object getPreviewValue(Component component, Field field, DesignerModelBinding<?> modelBinding,
                                  String bindingExpression, String bindingStaticValue, String propertyDefaultValue);

}
