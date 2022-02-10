package pl.fhframework.dp.commons.fh.document.list.searchtemplate;

import java.util.List;

public interface ISearchTemplateCriteriaProvider<DTO> {
    public List<Column> getAvailableColumns();
}
