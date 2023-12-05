package pl.fhframework.model.forms.provider;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

public class DictionaryLookupLegacyProvider<DIC_ELEMENT_TYPE, FORM_MODEL_BINDING_TYPE> implements IDictionaryLookupProvider<DIC_ELEMENT_TYPE, FORM_MODEL_BINDING_TYPE> {
    private final IComboDataProviderFhDP<DIC_ELEMENT_TYPE, FORM_MODEL_BINDING_TYPE> legacyComboDataProviderFhDP;
    private final Method getValuesPaged;
    private final Method getTitle;
    private final Method getValue;

    private final List<String> attributeNamesForGetValuesPaged;
    private final List<String> attributeNamesForGetTitle;
    private final List<String> attributeNamesForGetValue;

    public DictionaryLookupLegacyProvider(IComboDataProviderFhDP<DIC_ELEMENT_TYPE, FORM_MODEL_BINDING_TYPE> legacyComboDataProviderFhDP) {
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
        boolean searchByCode = searchText.length() > 1 && searchText.toUpperCase().equals(searchText);
        if (searchByCode) {//search by code
            FORM_MODEL_BINDING_TYPE modelValue = (FORM_MODEL_BINDING_TYPE) searchText; //TODO: Tutaj trzeba zrobić coś bardziej inteligentnego
            final DIC_ELEMENT_TYPE foundObject = this.getElementByModelValue(modelValue, externalAttributesValuesProvider);
            return new PageModel<>(pg -> getPageWithOneElement(foundObject));
        } else {//Search by content
            Object[] attributes = getAttributesValues(attributeNamesForGetValuesPaged, externalAttributesValuesProvider, searchText, pageable);
            return (PageModel<DIC_ELEMENT_TYPE>) ReflectionUtils.run(this.getValuesPaged, this.legacyComboDataProviderFhDP, attributes);
        }
    }

    @Override
    public String getDisplayValue(DIC_ELEMENT_TYPE dictionaryElement) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.out.println("getDisplayValue(DIC_ELEMENT_TYPE) used by: "+stackTraceElements[2].toString());
        if (dictionaryElement != null) {//Used by serviceOnSelectItem, getPresentText
            return legacyComboDataProviderFhDP.getDisplayValue(dictionaryElement);
        } else {
            return "";
        }
    }

    @Override
    public String getDisplayCode(FORM_MODEL_BINDING_TYPE value) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.out.println("getDisplayCode(FORM_MODEL_BINDING_TYPE) used by: "+stackTraceElements[2].toString());
        if (value == null) {
            return "";
        } else if (value instanceof String) {
            return (String) value; //In most cases value is just wanted code.
        } else {
            //TODO: Jak obsłużyć sytuację, gdy wartość w modelu nie jest powiązana z kodem a jakimś bardiej złożonym obiektem?
            throw new NotImplementedException("Sorry, I have no idea how to convert object '" + value.getClass().getName() + "' into string representing code!");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getDisplayValueByElement(Object dictionaryElement) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.out.println("getDisplayValue(Object) used by: "+stackTraceElements[2].toString());
        return getDisplayValue((DIC_ELEMENT_TYPE) dictionaryElement);
    }

    @Override
    public FORM_MODEL_BINDING_TYPE getModelValue(DIC_ELEMENT_TYPE dictionaryElement) {
        return legacyComboDataProviderFhDP.getCode(dictionaryElement);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DIC_ELEMENT_TYPE getElementByModelValue(FORM_MODEL_BINDING_TYPE modelValue, Function<String, Object> externalAttributesValuesProvider) {
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

    private Page<DIC_ELEMENT_TYPE> getPageWithOneElement(DIC_ELEMENT_TYPE pageContent) {
        if (pageContent != null) {
            return new PageImpl<>(Collections.singletonList(pageContent));
        } else {
            return Page.empty();
        }
    }
}