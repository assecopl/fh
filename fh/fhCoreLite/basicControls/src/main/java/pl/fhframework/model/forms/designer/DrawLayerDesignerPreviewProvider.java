package pl.fhframework.model.forms.designer;

import pl.fhframework.core.maps.features.geometry.Point;
import pl.fhframework.binding.DesignerModelBinding;
import pl.fhframework.model.forms.Component;

import java.lang.reflect.Field;

public class DrawLayerDesignerPreviewProvider implements IDesignerPreviewProvider {
    @Override
    public Object getPreviewValue(Component component, Field field, DesignerModelBinding<?> modelBinding,
                                  String bindingExpression, String bindingStaticValue, String propertyDefaultValue) {
        if (bindingStaticValue != null) {
            return bindingStaticValue;
        } else {
            return null;
        }
    }
}
