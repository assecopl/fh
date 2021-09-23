package pl.fhframework.model.forms.designer;

import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.model.forms.Component;
import pl.fhframework.tools.loading.FormWriter;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Default designer's preview binding value provider.
 */
public class DefaultDesignerFixedValuesProvider implements IDesignerFixedValuesProvider {

    public static final String EMPTY_BINDING_OPTION = new String("{...}");

    @Override
    public List<String> getFixedValues(Class<? extends Component> componentClass, Field field) {
        Class<?> fieldType = field.getType();
        Class<?> propertyType;
        boolean isBinding;

        DesignerXMLProperty annotation = field.getAnnotation(DesignerXMLProperty.class);
        if (ModelBinding.class.isAssignableFrom(fieldType)) {
            isBinding = true;
            propertyType = getBindingExpectedType(field, annotation);
        } else {
            isBinding = false;
            propertyType = fieldType;
        }

        // gather values based on property type
        List<String> values = new ArrayList<>();

        if (propertyType == boolean.class || propertyType == Boolean.class) {
            values.add("true");
            values.add("false");
        } else if (propertyType.isEnum()) {
            for (Enum value : ((Class<? extends Enum>) propertyType).getEnumConstants()) {
                // use converter if present (some enums are converted eg. to lowercase)
                values.add(FormWriter.convertToXmlAttributeValue(componentClass, field, value));
            }
        }

        // add empty binding
        if (!values.isEmpty() && isBinding) {
            values.add(EMPTY_BINDING_OPTION);
        }
        return values;
    }

    @Override
    public boolean isFreeTypingAllowed(Class<? extends Component> componentClass, Field field) {
        // field type - type of the field (for binding it would be ModelBinding)
        Class<?> fieldType = field.getType();
        if (fieldType == boolean.class || fieldType == Boolean.class
                || fieldType.isEnum()) {
            return false;
        } else {
            return true;
        }
    }

    private Class<?> getBindingExpectedType(Field field, DesignerXMLProperty annotation) {
        if (annotation != null && annotation.allowedTypes().length == 1 && annotation.allowedTypes()[0] != Object.class) {
            return annotation.allowedTypes()[0];
        } else {
            Class<?> bindingGenericType = ReflectionUtils.getGenericTypeInFieldType(field, 0);
            return bindingGenericType != null ? bindingGenericType : Object.class;
        }
    }

    @Override
    public boolean isFilteringAllowed(Class<? extends Component> componentClass, Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType == boolean.class || fieldType == Boolean.class
                || fieldType.isEnum()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isEmptyValueAllowed(Class<? extends Component> componentClass, Field field) {
        return !field.getType().isPrimitive();
    }
}
