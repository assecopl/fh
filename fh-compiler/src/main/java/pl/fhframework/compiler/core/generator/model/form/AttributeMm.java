package pl.fhframework.compiler.core.generator.model.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.DocumentedComponentAttribute;
import pl.fhframework.annotations.TwoWayBinding;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.*;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.fhframework.core.util.StringUtils.removeSurroundingBraces;
import static pl.fhframework.core.util.StringUtils.removeSurroundingCharacters;

@Getter
@Setter
@AllArgsConstructor
public class AttributeMm {
    protected static final String STRING_LITERAL_TEMPLATE = "'%s'";

    @JsonIgnore
    private String name;

    private AttributeType type;
    private String value;

    public static Optional<AttributeMm> fromField(Component component, Field field) {
        XMLProperty attrAnnotation = field.getAnnotation(XMLProperty.class);

        Component.IGenerationUtils generationUtils = component.getGenerationUtils();
        if (generationUtils == null) {
            return Optional.empty();
        }

        Object fieldValue = generationUtils.getFieldValue(field);
        if (fieldValue == null) {
            return Optional.empty();
        }

        String defaultValue = getDefaultValue(attrAnnotation, field);
        if (Objects.equals(defaultValue, fieldValue.toString())) {
            return Optional.empty();
        }

        String attrName = attrAnnotation.value();
        if (attrName.equals("")) {
            attrName = field.getName();
        }

        if (fieldValue instanceof ActionBinding) {
            return resolveActionBinding(attrName, (ActionBinding) fieldValue);
        } else if (fieldValue instanceof ModelBinding<?>) {
            return resolveModelBinding(attrName, (ModelBinding<?>) fieldValue, component, field);
        } else {
            return Optional.of(new AttributeMm(attrName, getStaticType(field.getGenericType()), surroundStringLiteral(String.valueOf(fieldValue.toString()), field.getGenericType())));
        }
    }

    private static String getDefaultValue(XMLProperty attrAnnotation, Field field) {
        if (!StringUtils.isNullOrEmpty(attrAnnotation.defaultValue()) ){
            return attrAnnotation.defaultValue();
        }
        DocumentedComponentAttribute dca = field.getAnnotation(DocumentedComponentAttribute.class);
        if (dca != null && !StringUtils.isNullOrEmpty(dca.defaultValue())) {
            return dca.defaultValue();
        }
        if (boolean.class.isAssignableFrom(field.getType())) {
            return "false";
        }
        return null;
    }

    private static Optional<AttributeMm> resolveActionBinding(String attrName, ActionBinding fieldValue) {
        if (fieldValue instanceof AdHocActionBinding) {
            AdHocActionBinding adHocActionBinding = (AdHocActionBinding) fieldValue;
            String actionBindingExpression = adHocActionBinding.getActionBindingExpression();
            return Optional.of(new AttributeMm(attrName, AttributeType.ActionBinding, actionBindingExpression));
        }

        throw new UnsupportedOperationException("Unknown action binding type: " + fieldValue.getClass().getName());
    }

    private static Optional<AttributeMm> resolveModelBinding(String attrName, ModelBinding<?> fieldValue, Component component, Field field) {
        Class<?> targetBindingType = ReflectionUtils.getGenericTypeInFieldType(field, 0);

        if (fieldValue instanceof AdHocModelBinding) {
            AdHocModelBinding<?> adHocModelBinding = ((AdHocModelBinding<?>) fieldValue);
            if (adHocModelBinding.getStaticValueText() == null) {
                if (adHocModelBinding.isCombined()) {
                    String value = adHocModelBinding.getCombinedExpressions().stream().map(exp -> exp.isBinding() ? removeSurroundingBraces(exp.getValue()) : surroundStringLiteral(exp.getValue(), targetBindingType)).collect(Collectors.joining(" + "));
                    return Optional.of(new AttributeMm(attrName, AttributeType.CombinedExpression, value));
                }
                else {
                    // TwoWayBinding is only for selected attribute
                    AttributeType type = AttributeType.OneWayBinding;
                    if (field.getAnnotation(TwoWayBinding.class) != null) {
                        type = AttributeType.TwoWayBinding;
                    }
                    return Optional.of(new AttributeMm(attrName, type, removeSurroundingBraces(adHocModelBinding.getBindingExpression())));
                }
            } else {
                return Optional.of(new AttributeMm(attrName, getStaticType(targetBindingType), surroundStringLiteral(adHocModelBinding.getStaticValueText(), targetBindingType)));
            }
        } else if (fieldValue instanceof StaticBinding) {
            String bindingTextValue = surroundStringLiteral(((StaticBinding<?>) fieldValue).getStaticValue().toString(), targetBindingType);
            return Optional.of(new AttributeMm(attrName, getStaticType(targetBindingType), bindingTextValue));
        } else if (fieldValue instanceof CompiledBinding) {
            String bindingTextValue = fieldValue.getBindingExpression();
            return Optional.of(new AttributeMm(attrName, getStaticType(targetBindingType), bindingTextValue));
        }

        throw new UnsupportedOperationException("Unknown model binding type: " + fieldValue.getClass().getName());
    }

    private static AttributeType getStaticType(Type targetBindingType) {
        return isStringLiteral(targetBindingType) ? AttributeType.StringLiteral : AttributeType.Static;
    }

    private static String surroundStringLiteral(String staticValueText, Type valueType) {
        Class<?> rawClass = ReflectionUtils.getRawClass(valueType);
        if (rawClass != null && Collection.class.isAssignableFrom(rawClass)) {
            staticValueText = removeSurroundingCharacters(staticValueText); // before "[a,b,c]"
        }
        return isStringLiteral(valueType) ? String.format(STRING_LITERAL_TEMPLATE, staticValueText.replace("'", "\"")) : staticValueText;
    }

    private static boolean isStringLiteral(Type valueType) {
        Class<?> rawClass = ReflectionUtils.getRawClass(valueType);
        return valueType == null ||
                String.class.isAssignableFrom(rawClass) ||
                rawClass.isEnum() ||
                Collection.class.isAssignableFrom(rawClass) && valueType instanceof ParameterizedType && String.class.isAssignableFrom(ReflectionUtils.tryGetGenericArgumentsRawClasses(valueType)[0]);
    }

    public enum AttributeType {
        StringLiteral, Static, OneWayBinding, TwoWayBinding, ActionBinding, CombinedExpression
    }
}
