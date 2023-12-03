package pl.fhframework.fhdp.example.lookup.provider;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.fhframework.dp.transport.dto.commons.NameValueDto;
import pl.fhframework.model.forms.PageModel;

import java.util.List;

public class CountryNameValuePagedTableSource {
    List<NameValueDto> countries;

    public CountryNameValuePagedTableSource(List<NameValueDto> countries) {
        this.countries = countries;
    }

    public PageModel<NameValueDto> createPagedModel(Pageable pageable) {
        //TODO: Do wyjaśnienia z Jackiem po co przekazywany jest ten interfjes pageable. To miałoby sens gdybyśmy tutaj roblili refresha na pagemodel...
        return new PageModel<>(t -> loadPage(t));
    }

    private Page<NameValueDto> loadPage(Pageable pageable) {
        int start = Math.toIntExact(pageable.getOffset());
        int end = Math.min((start + pageable.getPageSize()), countries.size());
        return new PageImpl<>(countries.subList(start, end), pageable, countries.size());
    }
}
