package pl.fhframework.core.rules.builtin;

import pl.fhframework.core.rules.BusinessRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Changes text CSV-like data into CvsRow list. May be used for filling tables in fast prototyping.
 * Input data should contain values for columns separated with semicolon and rows separated with pipe | character.
 */
@BusinessRule(categories = {"collection", "csv"})
public class CsvRows {

    /**
     * Changes text CSV-like data into CvsRow list. May be used for filling tables in fast prototyping.
     * @param csvData Input data that should contain values for columns separated with semicolon and rows separated with pipe | character.
     * @return list of csv rows
     */
    public List<CsvRow> csvRows(String csvData) {
        List<CsvRow> rows = new ArrayList<>();
        if (csvData != null && !csvData.isEmpty()) {
            for (String rowData : csvData.split("\\|")) {
                CsvRow row = new CsvRow();
                for (String columnData : rowData.split("\\;")) {
                    row.getValues().add(columnData);
                }
                rows.add(row);
            }
        }
        return rows;
    }
}
