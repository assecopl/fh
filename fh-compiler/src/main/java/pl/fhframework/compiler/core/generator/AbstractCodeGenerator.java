package pl.fhframework.compiler.core.generator;

import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.model.forms.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCodeGenerator {
    protected static final Map<String, Optional<Method>> fieldGettersCache = new ConcurrentHashMap<>();
    protected Set<String> usedFieldNames = new HashSet<>();

    private Map<AbstractJavaCodeGenerator.ComponentWrapper, LinkedList<String>> componentFields = new LinkedHashMap<>();

    public boolean hasAssignedName(Object component) {
        return componentFields.containsKey(new AbstractJavaCodeGenerator.ComponentWrapper(component));
    }

    protected void addFixedFieldMapping(String fieldName, Object object) {
        componentFields.put(new AbstractJavaCodeGenerator.ComponentWrapper(object), new LinkedList<>(Arrays.asList(fieldName)));
        usedFieldNames.add(fieldName);
    }

    public String assignLocalAlias(String suggestedLocalName, Object object) {
        String fieldName = reserveFieldName(suggestedLocalName, "v_");
        componentFields.get(new AbstractJavaCodeGenerator.ComponentWrapper(object)).push(fieldName);
        return fieldName;
    }

    protected String reserveFieldName(String suggestedName, String prefix) {
        String fieldName = suggestedName;
        if (!fieldName.startsWith(prefix)) {
            fieldName = prefix + fieldName;
        }
        fieldName = normalizeFieldName(fieldName);
        if (usedFieldNames.contains(fieldName)) {
            int counter = 1;
            while (usedFieldNames.contains(fieldName + "_" + counter)) {
                counter++;
            }
            fieldName = fieldName + "_" + counter;
        }
        usedFieldNames.add(fieldName);
        return fieldName;
    }

    public void unassignLocalAlias(Object object) {
        usedFieldNames.remove(componentFields.get(new AbstractJavaCodeGenerator.ComponentWrapper(object)).pop());
    }

    protected String getIdForException(Component component) {
        while (component.isArtificial() && component.getGroupingParentComponent() != null) {
            component = (Component) component.getGroupingParentComponent();
        }
        return component.getId();
    }

    protected Method getGetter(Class targetClass, Field field, Class<? extends Annotation> annotationClass) {
        String cacheKey = targetClass.getName() + "." + field.getName();
        if (!fieldGettersCache.containsKey(cacheKey)) {
            fieldGettersCache.put(cacheKey, ReflectionUtils.findGetter(targetClass, field));
        }
        Optional<Method> getter = fieldGettersCache.get(cacheKey);
        if (!getter.isPresent()) {
            throw new RuntimeException(String.format(
                    "Field %s.%s is annotated with %s but doesn't have a public getter",
                    field.getDeclaringClass().getName(), field.getName(),
                    annotationClass.getSimpleName()));
        }
        return getter.get();
    }

    public String resolveName(Object component) {
        return componentFields.get(new AbstractJavaCodeGenerator.ComponentWrapper(component)).peek();
    }

    protected Object getGetterValue(Object component, Class targetClass, Field field, Class<? extends Annotation> annotationClass) {
        Method getter = getGetter(targetClass, field, annotationClass);
        try {
            return getter.invoke(component);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Exception while getting value from field %s.%s",
                    field.getDeclaringClass().getName(), field.getName()), e);
        }
    }

    protected abstract Set<Character> getAllowedChars();
    protected abstract char getFieldReplacementChar();

    protected String normalizeFieldName(String name) {
        StringBuilder result = new StringBuilder(name.length());
        for (char singleChar : name.toCharArray()) {
            if (!getAllowedChars().contains(singleChar)) {
                singleChar = getFieldReplacementChar();
            }
            result.append(singleChar);
        }
        return result.toString();
    }

    protected String getBaseName(String fullName) {
        return DynamicClassName.forClassName(fullName).getBaseClassName();
    }
}
