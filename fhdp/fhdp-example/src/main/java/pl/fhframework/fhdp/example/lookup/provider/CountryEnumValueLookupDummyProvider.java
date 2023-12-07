package pl.fhframework.fhdp.example.lookup.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.dp.transport.dto.commons.NameValueDto;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.model.forms.provider.IDictionaryLookupProvider;
import pl.fhframework.model.forms.provider.NameValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CountryEnumValueLookupDummyProvider implements IDictionaryLookupProvider<NameValueDto, CountryNameValueLookupDummyProvider.CountryEnum> {
    private CountryNameValueLookupDummyProvider realProvider;
    @Override
    public PageModel<NameValueDto> getDictionaryElementsPaged(String searchText, Pageable pageable, Function<String, Object> externalAttributesValuesProvider) {
        return realProvider.getDictionaryElementsPaged(searchText, pageable, externalAttributesValuesProvider);
    }

    @Override
    public String getDisplayValue(NameValueDto dictionaryElement) {
        return realProvider.getDisplayValue(dictionaryElement);
    }

    @Override
    public String getDisplayCode(CountryNameValueLookupDummyProvider.CountryEnum value) {
        return value.getCodeAlfa2();
    }

    @Override
    public String getDisplayValueByElement(Object dictionaryElement) {
        return this.getDisplayValue((NameValueDto) dictionaryElement);
    }

    @Override
    public CountryNameValueLookupDummyProvider.CountryEnum getModelValue(NameValueDto dictionaryElement) {
        return Arrays.stream(CountryNameValueLookupDummyProvider.CountryEnum.values())
                .filter(country -> country.getCodeAlfa2().equals(dictionaryElement.getName()))
                .findFirst().orElse(null);
    }

    @Override
    public NameValueDto getElementByModelValue(CountryNameValueLookupDummyProvider.CountryEnum modelValue, Function<String, Object> externalAttributesValuesProvider) {
        String regionFilter = (String)externalAttributesValuesProvider.apply("regionFilter");
        return Arrays.stream(CountryNameValueLookupDummyProvider.CountryEnum.values())
                .filter(country-> (regionFilter==null||regionFilter.isEmpty()) || regionFilter.equals(country.getRegion()))
                .filter(country -> country.getCodeAlfa2().equals(modelValue.getCodeAlfa2()) || country.getCodeAlfa3().equals(modelValue.getCodeAlfa3()))
                .map(country -> new NameValueDto(country.getCodeAlfa2(), country.getPolishName()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<NameValue> getColumnDefinitions() {
        return realProvider.getColumnDefinitions();
    }

    @Override
    public String getTitle(Function<String, Object> externalAttributesValuesProvider) {
        return realProvider.getTitle(externalAttributesValuesProvider);
    }
}
