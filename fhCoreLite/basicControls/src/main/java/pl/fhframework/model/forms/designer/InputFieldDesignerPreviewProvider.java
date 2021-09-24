package pl.fhframework.model.forms.designer;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.binding.DesignerModelBinding;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.Combo;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.InputText;

import java.lang.reflect.Field;

/**
 * Input text designer's preview binding value provider.
 */
public class InputFieldDesignerPreviewProvider extends I18nAwareDesignerPreviewProvider {

    @Autowired
    private RulesService rulesService;

    @Autowired
    private MessageService messageService;

    public InputFieldDesignerPreviewProvider() {
        AutowireHelper.autowire(this, rulesService);
    }

    @Override
    public Object getPreviewValue(Component component, Field field, DesignerModelBinding<?> modelBinding,
                                  String bindingExpression, String bindingStaticValue, String propertyDefaultValue) {
        if (bindingStaticValue != null) {
            return bindingStaticValue;
        } else {
            if (component instanceof InputText || component instanceof Combo) {
                String shortExpression = rulesService.convertToShortNames(bindingExpression);
                if (component.getForm().isDesignMode()) {
                    return shortExpression;
                } else {
                    return getI18nBindingValue(messageService, component.getForm(), modelBinding).orElse(shortExpression);
                }
            } else {
                return null;
            }
        }
    }
}
