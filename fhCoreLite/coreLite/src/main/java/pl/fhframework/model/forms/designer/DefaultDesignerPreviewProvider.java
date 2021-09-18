package pl.fhframework.model.forms.designer;

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;

import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.binding.DesignerModelBinding;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.Component;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Default designer's preview binding value provider.
 */
public class DefaultDesignerPreviewProvider implements IDesignerPreviewProvider {

    @Autowired
    private FhConversionService conversionService;

    public DefaultDesignerPreviewProvider() {
        AutowireHelper.autowire(this, conversionService);
    }

    @Override
    public Object getPreviewValue(Component component, Field field, DesignerModelBinding<?> modelBinding,
                           String bindingExpression, String bindingStaticValue, String propertyDefaultValue) {
        DesignerXMLProperty designerXMLProperty = field.getAnnotation(DesignerXMLProperty.class);
        if (designerXMLProperty == null) {
            designerXMLProperty = DesignerXMLProperty.Defaults.getDefaults();
        }
        List<Class> classes = Arrays.asList(designerXMLProperty.allowedTypes());

        if (classes.contains(Collection.class)) {
            return new LinkedList<>();
        } else if (classes.contains(Map.class)) {
            return new HashMap<>();
        } else if (bindingStaticValue != null) {
            final Class<?> genericTypeInFieldType = ReflectionUtils.getGenericTypeInFieldType(field, 0);
            if (genericTypeInFieldType != null) {
                if(conversionService.canConvert(String.class, genericTypeInFieldType)) {
                    try {
                        return conversionService.convert(bindingStaticValue, genericTypeInFieldType);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return bindingStaticValue;
            }
        } else {
            return null;
        }
    }
}
