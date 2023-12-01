package pl.fhframework.fhdp.example.lookup.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.dp.transport.dto.commons.NameValueDto;
import pl.fhframework.fhdp.example.lookup.model.Country;
import pl.fhframework.fhdp.example.lookup.rest.CountryApiResponse;
import pl.fhframework.fhdp.example.lookup.rest.CountryEntity;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.model.forms.provider.IComboDataProviderFhDP;
import pl.fhframework.model.forms.provider.IDictionaryLookupProvider;
import pl.fhframework.model.forms.provider.NameValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CountryNameValueLookupDummyLegacyProvider implements IComboDataProviderFhDP<NameValueDto, String> {
    private CountryNameValueLookupDummyProvider realProvider;

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
                                 @Parameter(name = "regionFilter") String region
    ) {
        return realProvider.getElementByModelValue(code, (attrName) -> {
            switch (attrName) {
                case "regionFilter":
                    return region;
                default:
                    throw new RuntimeException("Unknown attribute name '" + attrName + '!');
            }
        });
    }

    public String getTitle() {
        return realProvider.getTitle(null);
    }

    @Override
    public String getCode(NameValueDto element) {
        return realProvider.getModelValue(element);
    }

    @Override
    public String getDisplayValue(NameValueDto element) {
        return realProvider.getDisplayValue(element);
    }

    @Override
    public Boolean areObjectsEquals(String element1, String element2) {
        return Objects.equals(element1, element2);
    }

    @Override
    public List<NameValue> getColumnDefinitions() {
        return realProvider.getColumnDefinitions();
    }

    @Override
    public String initResultFromKey(String key) {
        return null;
    }

    @Override
    public String getSrcKey(NameValueDto element) {
        return element.getName();
    }
}
