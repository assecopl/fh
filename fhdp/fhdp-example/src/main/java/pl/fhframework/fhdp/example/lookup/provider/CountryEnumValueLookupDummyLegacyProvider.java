package pl.fhframework.fhdp.example.lookup.provider;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.dp.transport.dto.commons.NameValueDto;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.model.forms.provider.IComboDataProviderFhDP;
import pl.fhframework.model.forms.provider.IDictionaryLookupProvider;
import pl.fhframework.model.forms.provider.NameValue;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@Slf4j
@AllArgsConstructor
public class CountryEnumValueLookupDummyLegacyProvider implements IComboDataProviderFhDP<NameValueDto, CountryNameValueLookupDummyProvider.CountryEnum> {
    private CountryEnumValueLookupDummyProvider realProvider;

    public PageModel<NameValueDto> getValuesPaged(String searchText, Pageable pageable,
                                                  @Parameter(name = "regionFilter") String region
    ) {
        return realProvider.getDictionaryElementsPaged(searchText, pageable, (attrName) -> {
            switch (attrName) {
                case "regionFilter":
                    return region;
                default:
                    throw new RuntimeException("Unknown attribute name '" + attrName + '!');
            }
        });
    }

    public NameValueDto getValue(String code,
                                 @Parameter(name = "regionFilter") String regionFilter
    ) {
        return Arrays.stream(CountryNameValueLookupDummyProvider.CountryEnum.values())
                .filter(country -> (regionFilter == null || regionFilter.isEmpty()) || regionFilter.equals(country.getRegion()))
                .filter(country -> country.getCodeAlfa2().equals(code))
                .map(country -> new NameValueDto(country.getCodeAlfa2(), country.getPolishName()))
                .findFirst()
                .orElse(null);
    }

    public String getTitle() {
        return realProvider.getTitle(null);
    }

    @Override
    public CountryNameValueLookupDummyProvider.CountryEnum getCode(NameValueDto element) {
        return realProvider.getModelValue(element);
    }

    @Override
    public String getDisplayValue(NameValueDto element) {
        return realProvider.getDisplayValue(element);
    }

    @Override
    public Boolean areObjectsEquals(CountryNameValueLookupDummyProvider.CountryEnum element1, CountryNameValueLookupDummyProvider.CountryEnum element2) {
        return Objects.equals(element1, element2);
    }

    @Override
    public List<NameValue> getColumnDefinitions() {
        return realProvider.getColumnDefinitions();
    }

    @Override
    public CountryNameValueLookupDummyProvider.CountryEnum initResultFromKey(String key) {
        return Arrays.stream(CountryNameValueLookupDummyProvider.CountryEnum.values())
                .filter(country -> country.getCodeAlfa2().equals(key))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getSrcKey(NameValueDto element) {
        return element.getName();
    }
}
