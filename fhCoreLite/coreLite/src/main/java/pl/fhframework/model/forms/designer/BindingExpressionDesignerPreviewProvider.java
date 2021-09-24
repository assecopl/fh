package pl.fhframework.model.forms.designer;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.binding.DesignerModelBinding;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.Component;

import java.lang.reflect.Field;

/**
 * Default designer's preview binding value provider.
 */
public class BindingExpressionDesignerPreviewProvider extends I18nAwareDesignerPreviewProvider {

    @Autowired
    private RulesService rulesService;

    @Autowired
    private MessageService messageService;

    public BindingExpressionDesignerPreviewProvider() {
        AutowireHelper.autowire(this, rulesService);
    }

    @Override
    public Object getPreviewValue(Component component, Field field, DesignerModelBinding<?> modelBinding,
                                  String bindingExpression, String bindingStaticValue, String propertyDefaultValue) {
        if (bindingStaticValue != null) {
            return bindingStaticValue;
        } else {
            String shortExpression = rulesService.convertToShortNames(bindingExpression);
            if (component.getForm().isDesignMode()) {
                return shortExpression;
            } else {
                return getI18nBindingValue(messageService, component.getForm(), modelBinding).orElse(shortExpression);
            }
        }
    }
}
