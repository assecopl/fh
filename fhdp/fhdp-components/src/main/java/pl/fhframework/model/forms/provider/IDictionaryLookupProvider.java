package pl.fhframework.model.forms.provider;

import org.springframework.data.domain.Pageable;
import pl.fhframework.model.forms.PageModel;

import java.util.List;

public interface IDictionaryLookupProvider <DIC_ELEMET_TYPE>{
    PageModel<DIC_ELEMET_TYPE> getValuesPaged(String searchText, Pageable pageable, Object... param);

    String getDisplayValue(DIC_ELEMET_TYPE element);

    String getElementId(DIC_ELEMET_TYPE element);

    DIC_ELEMET_TYPE getElementById(String elementId);

    List<NameValue> getColumnDefinitions();
}
