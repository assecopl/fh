package pl.fhframework.model.forms.attributes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.util.ClassUtils;

import pl.fhframework.model.dto.ElementChanges;
import pl.fhframework.model.dto.ValueChange;
import pl.fhframework.model.forms.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class AttributeHolder {
    @JsonSerialize(using = AttributeHolderSerializer.class)
    private Map<Class<? extends Attribute>, Attribute> attributes = new LinkedHashMap<>();

    @JsonIgnore
    private Map<Class<? extends BoundableAttribute>, BoundableAttribute> boundableAttributes = new LinkedHashMap<>();

    public AttributeHolder() {
    }

    public void addAttribute(Attribute attribute) {
        attributes.put(attribute.getClass(), attribute);
        if (ClassUtils.isAssignableValue(BoundableAttribute.class, attribute)) {
            BoundableAttribute boundableAttribute = (BoundableAttribute) attribute;
            boundableAttributes.put(boundableAttribute.getClass(), boundableAttribute);
        }
    }

    public ElementChanges updateView(Component component, ElementChanges elementChanges, Class<? extends BoundableAttribute> pinnedAttributeClass) {
        final BoundableAttribute attribute = boundableAttributes.get(pinnedAttributeClass);
        if (attribute != null) {
            return attribute.updateView(component, elementChanges);
        }
        return elementChanges;
    }

    public ElementChanges updateView(Component component, ElementChanges elementChanges) {
        final Collection<BoundableAttribute> attributes = boundableAttributes.values();
        for (BoundableAttribute attribute : attributes) {
            elementChanges = attribute.updateView(component, elementChanges);
        }
        return elementChanges;
    }

    public void updateModel(Component component, ValueChange valueChange, Class<? extends BoundableAttribute> pinnedAttributeClass) {
        final BoundableAttribute attribute = boundableAttributes.get(pinnedAttributeClass);
        if (attribute != null) {
            attribute.updateModel(component, valueChange);
        }
    }

    public void updateModel(Component component, ValueChange valueChange) {
        final Collection<BoundableAttribute> attributes = boundableAttributes.values();
        for (BoundableAttribute attribute : attributes) {
            if (attribute != null) {
                attribute.updateModel(component, valueChange);
            }
        }
    }

    public <T> T get(Class<T> attributeClass) {
        return (T) attributes.get(attributeClass);
    }
}
