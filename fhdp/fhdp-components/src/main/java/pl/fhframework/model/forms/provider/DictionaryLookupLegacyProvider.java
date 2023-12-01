package pl.fhframework.model.forms.provider;

import org.springframework.data.domain.Pageable;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.model.forms.PageModel;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DictionaryLookupLegacyProvider<DIC_ELEMENT_TYPE, MODEL_TYPE> implements IDictionaryLookupProvider<DIC_ELEMENT_TYPE, MODEL_TYPE> {
    private final IComboDataProviderFhDP<DIC_ELEMENT_TYPE, MODEL_TYPE> legacyComboDataProviderFhDP;
    private final Method getValuesPaged;
    private final Method getTitle;
    private final Method getValue;

    private final List<String> attributeNamesForGetValuesPaged;
    private final List<String> attributeNamesForGetTitle;
    private final List<String> attributeNamesForGetValue;

    public DictionaryLookupLegacyProvider(IComboDataProviderFhDP<DIC_ELEMENT_TYPE, MODEL_TYPE> legacyComboDataProviderFhDP) {
        this.legacyComboDataProviderFhDP = legacyComboDataProviderFhDP;
        this.getValue = getProviderMethod("getValue");
        this.getValuesPaged = getProviderMethod("getValuesPaged");
        this.getTitle = getProviderMethod("getTitle");
        attributeNamesForGetValue = getAttributeNamesForMethodInOrder(this.getValue, 1);
        attributeNamesForGetValuesPaged = getAttributeNamesForMethodInOrder(this.getValuesPaged, 2);
        attributeNamesForGetTitle = getAttributeNamesForMethodInOrder(this.getTitle, 0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageModel<DIC_ELEMENT_TYPE> getDictionaryElementsPaged(String searchText, Pageable pageable, Function<String, Object> externalAttributesValuesProvider) {
        Object[] attributes = getAttributesValues(attributeNamesForGetValuesPaged, externalAttributesValuesProvider, searchText, pageable);
        return (PageModel<DIC_ELEMENT_TYPE>) ReflectionUtils.run(this.getValuesPaged, this.legacyComboDataProviderFhDP, attributes);
    }

    @Override
    public String getDisplayValue(DIC_ELEMENT_TYPE dictionaryElement) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getDisplayValueByElement(Object dictionaryElement) {
        return getDisplayValue((DIC_ELEMENT_TYPE) dictionaryElement);
    }

    @Override
    public MODEL_TYPE getModelValue(DIC_ELEMENT_TYPE dictionaryElement) {
        return legacyComboDataProviderFhDP.getCode(dictionaryElement);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DIC_ELEMENT_TYPE getElementByModelValue(MODEL_TYPE modelValue, Function<String, Object> externalAttributesValuesProvider) {
        Object[] attributes = getAttributesValues(attributeNamesForGetValue, externalAttributesValuesProvider, modelValue);
        return (DIC_ELEMENT_TYPE) ReflectionUtils.run(this.getValue, this.legacyComboDataProviderFhDP, attributes);
    }

    @Override
    public List<NameValue> getColumnDefinitions() {
        return legacyComboDataProviderFhDP.getColumnDefinitions();
    }

    @Override
    public String getTitle(Function<String, Object> externalAttributesValuesProvider) {
        Object[] attributes = getAttributesValues(attributeNamesForGetTitle, externalAttributesValuesProvider);
        return (String) ReflectionUtils.run(this.getTitle, this.legacyComboDataProviderFhDP, attributes);
    }

    private Method getProviderMethod(String methodName) {
        return ReflectionUtils
                .findMatchingPublicMethod(this.legacyComboDataProviderFhDP.getClass(), methodName)
                .orElseThrow(
                        () -> new RuntimeException("Sorry, but your dictionary provider in class " + this.legacyComboDataProviderFhDP.getClass().getName() + " doesn't provide required method '" + methodName + "'!")
                );
    }

    private List<String> getAttributeNamesForMethodInOrder(Method method, int numberOfAttributesWithoutAnnotation) {
//        return Arrays.stream(method.getParameters())
//                .map(parameter -> parameter.getAnnotation(Parameter.class))
//                .filter(parameter -> parameter.name()!=null && !parameter.name().isEmpty())
//                .map(Parameter::name)
//                .collect(Collectors.toList());
        final AtomicInteger counter = new AtomicInteger(0);
        return Arrays.stream(method.getParameters())
                .filter(parameter -> counter.getAndIncrement() >= numberOfAttributesWithoutAnnotation)
                .map(parameter -> parameter.getAnnotation(Parameter.class))
                .map(parameter -> {
                    if (parameter != null) {
                        if (parameter.name() == null || parameter.name().isEmpty()) {
                            throw new RuntimeException("Sorry, but your annotation Parameter in method '" + method.getName() + "' in your provider '" + this.legacyComboDataProviderFhDP.getClass().getName() + " must not have empty name parameter!");
                        }
                        return parameter.name();
                    } else {
                        throw new RuntimeException("Sorry, but every attribute in method '" + method.getName() + "' in your provider '" + this.legacyComboDataProviderFhDP.getClass().getName() + " must be annotated with @pl.fhframework.core.uc.Parameter!");
                    }
                })
                .collect(Collectors.toList());
    }

    private Object[] getAttributesValues(List<String> attributesNames, Function<String, Object> valuesResolver, Object... mainAttributesValues) {
        return Stream.concat(
                Arrays.stream(mainAttributesValues),
                attributesNames.stream().map(valuesResolver)
        ).toArray();
    }
}