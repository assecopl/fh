package pl.fhframework.model.forms.designer;

import pl.fhframework.binding.DesignerModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.OptionsList;
import pl.fhframework.model.forms.model.OptionsListElementModel;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class OptionsListDesignerPreviewProvider implements IDesignerPreviewProvider {

    @Override
    public Object getPreviewValue(Component component, Field field, DesignerModelBinding<?> modelBinding,
                           String bindingExpression, String bindingStaticValue, String propertyDefaultValue) {
        if (bindingStaticValue != null) {
            return bindingStaticValue;
        } else {
            if (component instanceof OptionsList) {
                ArrayList<OptionsListElementModel> result = new ArrayList<>();
                result.add(new OptionsListElementModel(1, bindingExpression));
                return result;
            } else {
                return null;
            }
        }
    }
}
