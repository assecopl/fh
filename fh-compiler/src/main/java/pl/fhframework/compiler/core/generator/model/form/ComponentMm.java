package pl.fhframework.compiler.core.generator.model.form;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.model.usecase.WithExpression;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.XMLMetadataSubelement;
import pl.fhframework.annotations.XMLMetadataSubelements;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.GroupingComponent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
@Setter
@JsonPropertyOrder({"type", "tagName", "availability", "attributes", "actions", "hasSubcomponents", "subcomponents"})
public class ComponentMm implements WithExpression {
    private static final Predicate<Field> IS_ATTRIBUTE_ACTION_PREDICATE = (c) -> c.getName().startsWith("on");

    private static final Function<String, String> STRIP_ACTION_NAME_FUNCTION = (c) -> {
        String name = c.substring(2);
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        return name;
    };

    @JsonIgnore
    private Component component;

    public ComponentMm(Component component) {
        this.component = component;
    }

    @JsonGetter
    public boolean hasSubcomponents() {
        boolean result = false;
        if (component instanceof GroupingComponent<?>) {
            result = !((GroupingComponent<?>) component).getSubcomponents().isEmpty();
            if (result) {
                return true;
            }
        } else if (!ReflectionUtils.getFieldsWithHierarchy(component.getClass(), XMLMetadataSubelements.class).isEmpty()) {
            return true;
        } else if (!ReflectionUtils.getFieldsWithHierarchy(component.getClass(), XMLMetadataSubelement.class).isEmpty()) {
            return true;
        }

        return result;
    }

    @JsonGetter
    public Collection<ComponentMm> getSubcomponents() {
        Collection<ComponentMm> result = new ArrayList<>();

        result.addAll(processFieldsWithAnnotation(component.getClass(), component, XMLMetadataSubelements.class));
        result.addAll(processFieldsWithAnnotation(component.getClass(), component, XMLMetadataSubelement.class));

        if (component instanceof GroupingComponent<?>) {
            GroupingComponent<?> groupingComponent = (GroupingComponent<?>) component;
            result.addAll(groupingComponent.getSubcomponents()
                    .stream()
                    .map(ComponentMm::new)
                    .collect(Collectors.toList()
                    )
            );
        }

        return result;
    }

    private Collection<ComponentMm> processFieldsWithAnnotation(Class<?> targetClass, Component component,
                                                                Class<? extends Annotation> annotationClass
    ) {
        Collection<ComponentMm> components = new ArrayList<>();
        for (Field field : ReflectionUtils.getFieldsWithHierarchy(targetClass, annotationClass)) {
            Object embeddedComponentOrCollection = component.getGenerationUtils().getFieldValue(field);
            if (embeddedComponentOrCollection == null) {
                continue;
            }

            if (embeddedComponentOrCollection instanceof Component) {
                components.add(new ComponentMm((Component) embeddedComponentOrCollection));
            } else if (embeddedComponentOrCollection instanceof Collection
                    && Component.class.isAssignableFrom(ReflectionUtils.getGenericTypeInFieldType(field, 0))) {
                for (Component embeddedComponent : (Collection<Component>) embeddedComponentOrCollection) {
                    components.add(new ComponentMm(embeddedComponent));
                }
            }
        }

        return components;
    }

    @JsonGetter
    public String getTagName() {
        Component.IGenerationUtils generationUtils = component.getGenerationUtils();
        if (generationUtils != null) {
            return generationUtils.getTagName();
        } else {
            return component.getClass().getSimpleName().toLowerCase();
        }
    }

    @JsonGetter
    public String getType() {
        return component.getClass().getSimpleName();
    }

    @JsonGetter
    public AccessibilityEnum getAvailability() {
        return component.getAvailability();
    }

    @JsonGetter
    public Map<String, AttributeMm> getActions() {
        return ReflectionUtils.getFieldsWithHierarchy(component.getClass(), XMLProperty.class)
                .stream()
                .filter(IS_ATTRIBUTE_ACTION_PREDICATE)
                .map(field -> AttributeMm.fromField(component, field))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(attr -> STRIP_ACTION_NAME_FUNCTION.apply(attr.getName()), Function.identity()));
    }

    @JsonGetter
    public Map<String, AttributeMm> getAttributes() {
        return ReflectionUtils.getFieldsWithHierarchy(component.getClass(), XMLProperty.class)
                .stream()
                .filter(IS_ATTRIBUTE_ACTION_PREDICATE.negate())
                .map(field -> AttributeMm.fromField(component, field))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(AttributeMm::getName, Function.identity()));
    }

    @Override
    public <T> T provideImpl() {
        return (T) component;
    }
}
