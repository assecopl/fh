package pl.fhframework.dp.commons.fh.parameters.list;

import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.fh.document.list.searchtemplate.Column;
import pl.fhframework.dp.commons.fh.document.list.searchtemplate.ISearchTemplateCriteriaProvider;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDto;

import java.util.List;

@Service
public class SearchTemplateCriteriaProviderSubstantiveParameters implements ISearchTemplateCriteriaProvider<SubstantiveParametersDto> {
//    @Autowired
//    private BaseAppMessageHelper baseAppMessageHelper;

    private String path = SubstantiveParametersDto.class.getName();

    //TODO: implement search
    @Override
    public List<Column> getAvailableColumns() {
        return null;
    }

    //TODO: wyszukiwanie parametrów zrealizowane będzie w późniejszym etapie
//    private String getI18nPath(String fieldName) {
//        String description = baseAppMessageHelper.getMessage(path + "." + fieldName);
//        if (!description.contains(" not found"))
//            return description;
//        else
//            return fieldName;
//    }
}
