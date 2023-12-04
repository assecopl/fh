package pl.fhframework.fhdp.example.lookup.provider;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.fhframework.fhdp.example.lookup.model.Country;
import pl.fhframework.model.forms.PageModel;

import java.util.List;

public class CountryPagedTableSource {
    List<Country> countries;

    public CountryPagedTableSource(List<Country> countries) {
        this.countries = countries;
    }

    public PageModel<Country> createPagedModel(Pageable pageable) {
        return new PageModel<>(t -> loadPage(t));
    }

    private Page<Country> loadPage(Pageable pageable) {
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), countries.size());
        return new PageImpl<>(countries.subList(start, end), pageable, countries.size());
    }
}
