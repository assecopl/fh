package pl.fhframework.model.forms.designer;

import pl.fhframework.binding.DesignerModelBinding;
import pl.fhframework.model.forms.Combo;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.InputText;

import java.lang.reflect.Field;

/**
 * Size in px designer's preview binding value provider.
 */
public class SizePxDesignerPreviewProvider implements IDesignerPreviewProvider {

    @Override
    public Object getPreviewValue(Component component, Field field, DesignerModelBinding<?> modelBinding,
                                  String bindingExpression, String bindingStaticValue, String propertyDefaultValue) {
        if (bindingStaticValue != null) {
            return bindingStaticValue;
        } else {
            return "350px";
        }
    }
}
