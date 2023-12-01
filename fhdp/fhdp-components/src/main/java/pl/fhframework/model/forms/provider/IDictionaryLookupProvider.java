package pl.fhframework.model.forms.provider;

import org.springframework.data.domain.Pageable;
import pl.fhframework.model.forms.PageModel;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface IDictionaryLookupProvider <DIC_ELEMENT_TYPE, MODEL_TYPE>{
    PageModel<DIC_ELEMENT_TYPE> getDictionaryElementsPaged(String searchText, Pageable pageable, Function<String, Object> externalAttributesValuesProvider);

    String getDisplayValue(DIC_ELEMENT_TYPE dictionaryElement);

    //String getDisplayValueByElementId(String elementId);

    String getDisplayValueByElement(Object dictionaryElement);

    MODEL_TYPE getModelValue(DIC_ELEMENT_TYPE dictionaryElement);

    DIC_ELEMENT_TYPE getElementByModelValue(MODEL_TYPE modelValue, Function<String, Object> externalAttributesValuesProvider);

    List<NameValue> getColumnDefinitions();

    String getTitle(Function<String, Object> externalAttributesValuesProvider);
}
