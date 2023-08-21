package pl.fhframework.fhdp.example.lookup.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.transport.dto.commons.NameValueDto;
import pl.fhframework.fhdp.example.lookup.rest.CountryApiResponse;
import pl.fhframework.fhdp.example.lookup.rest.CountryEntity;
import pl.fhframework.fhdp.example.lookup.rest.RestClient;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.model.forms.provider.IComboDataProviderFhDP;
import pl.fhframework.model.forms.provider.NameValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CountryNameValueLookupProvider implements IComboDataProviderFhDP<NameValueDto, String> {

    @Autowired
    RestClient restClient;

    @Override
    public String getCode(NameValueDto element) {
        if(element == null) return null;
        else return element.getValue();
    }

    @Override
    public String getDisplayValue(NameValueDto element) {
        return element.getValue() + ": " + element.getName();
    }

    @Override
    public Boolean areObjectsEquals(String element1, String element2) {
        if (element2 == element1) return true;
        if (element2 != null && element1 == null) return false;
        if (element2 == null) return false;
        return element1.equals(element2);
    }

    @Override
    public List<NameValue> getColumnDefinitions() {
        List<NameValue> columns = new ArrayList<>();
        columns.add(new NameValue("Code", "value"));
        columns.add(new NameValue("Name", "name"));
        return columns;
    }

    @Override
    public String getSrcKey(NameValueDto element) {
        if(element == null) return null;
        return element.getValue();
    }

    public PageModel<NameValueDto> getValuesPaged(String text, Pageable pageable) {
        List<NameValueDto> countries = new ArrayList<>();
        if(text != null && text.length() > 0) {
            CountryApiResponse result = restClient.listCountries(text);
            countries = getCountries(result);
        }
        return new CountryNameValuePagedTableSource(countries).createPagedModel(pageable);
    }

    public NameValueDto getValue(String code) {
        CountryApiResponse result = restClient.listCountries(code);
        if(result.getData() != null && result.getData().containsKey(code)) {
            CountryEntity entity = result.getData().get(code);
            return new NameValueDto(code, entity.getCountry());
        } else {
            return null;
        }
    }

    public String getTitle() {
        return "Countries";
    }

    private List<NameValueDto> getCountries(CountryApiResponse result) {
        List<NameValueDto> ret = new ArrayList<>();
        if(result.getData() != null) {
            Set<String> countryCodes = result.getData().keySet();
            for (String code : countryCodes) {
                CountryEntity entity = result.getData().get(code);
                NameValueDto country = new NameValueDto(code, entity.getCountry());
                ret.add(country);
            }
        }
        return ret;
    }

}
