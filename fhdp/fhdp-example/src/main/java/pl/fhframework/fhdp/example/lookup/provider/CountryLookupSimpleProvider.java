package pl.fhframework.fhdp.example.lookup.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.fhframework.fhdp.example.lookup.model.Country;
import pl.fhframework.fhdp.example.lookup.rest.CountryApiResponse;
import pl.fhframework.fhdp.example.lookup.rest.CountryEntity;
import pl.fhframework.fhdp.example.lookup.rest.RestClient;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.model.forms.provider.IComboDataProvider;
import pl.fhframework.model.forms.provider.IComboDataProviderFhDP;
import pl.fhframework.model.forms.provider.NameValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CountryLookupSimpleProvider implements IComboDataProvider<Country, String> {

    @Autowired
    RestClient restClient;

    @Override
    public String getCode(Country element) {
        return element.getCode();
    }

    @Override
    public String getDisplayValue(Country element) {
        return element.getName();
    }

    @Override
    public Boolean areObjectsEquals(String element1, String element2) {
        if (element2 == element1) return true;
        if (element2 != null && element1 == null) return false;
        if (element2 == null) return false;
        return element1.equals(element2);
    }


    public List<Country> getValues(String text) {
        CountryApiResponse result = restClient.listCountries(text);
        List<Country> countries = getCountries(result);
        return countries;
    }

    public Country getValue(String code) {
        CountryApiResponse result = restClient.listCountries(code);
        if (result.getData() != null && result.getData().containsKey(code)) {
            CountryEntity entity = result.getData().get(code);
            return new Country(code, entity.getCountry(), entity.getRegion());
        } else {
            return null;
        }
    }

    public String getTitle() {
        return "Countries";
    }

    private List<Country> getCountries(CountryApiResponse result) {
        List<Country> ret = new ArrayList<>();
        Set<String> countryCodes = result.getData().keySet();
        for (String code : countryCodes) {
            CountryEntity entity = result.getData().get(code);
            Country country = new Country(code, entity.getCountry(), entity.getRegion());
            ret.add(country);
        }
        return ret;
    }

}
