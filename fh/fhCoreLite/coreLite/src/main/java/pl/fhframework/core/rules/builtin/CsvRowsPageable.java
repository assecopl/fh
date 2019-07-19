package pl.fhframework.core.rules.builtin;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.model.forms.CollectionPageableModel;
import pl.fhframework.model.forms.PageModel;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Changes text CSV-like data into CvsRow page model. May be used for filling tables in fast prototyping.
 * Input data should contain values for columns separated with semicolon and rows separeted with pipe | character.
 */
@BusinessRule(categories = {"collection", "csv"})
public class CsvRowsPageable {

    @Autowired
    private CsvRows csvRows;

    // Cache returned instances as page model is statefull and in a dirty prototype this rule may be called again for every reference to model.
    // Always the same instance must be returned to keep track between setting page information and getting values in columns.
    private Map<String, PageModel<CsvRow>> pageModelCache = new WeakHashMap<>();

    /**
     * Changes text CSV-like data into CvsRow page model. May be used for filling tables in fast prototyping.
     * @param csvData Input data that should contain values for columns separated with semicolon and rows separeted with pipe | character.
     * @return page model of csv rows
     */
    public PageModel<CsvRow> csvRowsPageable(String csvData) {
        PageModel<CsvRow> model = pageModelCache.get(csvData);
        if (model == null) {
            model = new CollectionPageableModel<>(csvRows.csvRows(csvData));
            pageModelCache.put(csvData, model);
        }
        return model;
    }
}
