package pl.fhframework.dp.commons.fh.declaration.list.searchtemplate;

import java.util.List;

public interface ISearchTemplateCriteriaProvider<DTO> {
    public List<Column> getAvailableColumns();
}
