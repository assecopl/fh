package pl.fhframework.model.forms.csv;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.fhframework.SessionManager;
import pl.fhframework.core.FhException;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.io.FileService;
import pl.fhframework.io.TemporaryResource;
import pl.fhframework.model.forms.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final FileService fileService;
    private final EventRegistry eventRegistry;

    /** Exports Table form component values to CSV file */
    public void exportTableToCsv(Table table) {
        TemporaryResource resource = createTempResource();
        try (FileWriter output = new FileWriter(resource.getFile());
             CSVPrinter printer = new CSVPrinter(output, CSVFormat.EXCEL.withDelimiter(';'))) {

            // headers
            List<String> headers = table.getColumns().stream()
                    .map(Column::getLabel)
                    .collect(Collectors.toList());
            printer.printRecord(headers);

            // rows
            if (table instanceof TablePaged) {
                exportTablePagedRows(printer, (TablePaged) table);
            } else {
                table.getTableRows()
                    .forEach(row -> exportRow(printer, row));
            }

            eventRegistry.fireDownloadEvent(resource);
        } catch (IOException e) {
            throw new FhException("Error during Table to CSV export.", e);
        }
    }

    /** Creates temporary resource */
    @SuppressWarnings("ConstantConditions")
    private TemporaryResource createTempResource() {
        try {
            return fileService.createNewTemporaryResource(
                    UUID.randomUUID().toString() + ".csv",
                    SessionManager.getUserSession()
            ).getSecond();
        } catch (IOException e) {
            throw new FhException("Cannot create empty CSV file.", e);
        }
    }

    /** Exports rows for paged table */
    private void exportTablePagedRows(CSVPrinter printer, TablePaged tablePaged) {
        int oldPage = tablePaged.getPageNumber();
        int page = 0;
        do {
            PageRequest request = PageRequest.of(page, tablePaged.getPageSize());
            tablePaged.getPageModel().doRefresh(request);
            tablePaged.processComponents();
            tablePaged.updateView();

            // export rows for page
            tablePaged.getTableRows()
                    .forEach(row -> exportRow(printer, row));

            page++;
        } while (tablePaged.getPageModel().getPage().hasNext());

        // return to old page
        PageRequest request = PageRequest.of(oldPage, tablePaged.getPageSize());
        tablePaged.getPageModel().doRefresh(request);
    }

    /** Exports table row to CSV file */
    private void exportRow(CSVPrinter printer, TableRow tableRow) {
        StringBuilder builder;
        List<String> values = new ArrayList<>();
        for (FormElement element : tableRow.getTableCells()) {
            if (TableCell.class.isAssignableFrom(element.getClass())) {
                builder = new StringBuilder();
                for (FormElement formElement : ((TableCell) element).getSubcomponents()) {
                    if (formElement instanceof OutputLabel) {
                        OutputLabel outputLabel = (OutputLabel) formElement;
                        outputLabel.updateView();
                        if (outputLabel.getValue() != null) {
                            if (builder.length() == 0) {
                                builder.append(outputLabel.getValue());
                            } else {
                                builder.append(" ")
                                       .append(outputLabel.getValue());
                            }
                        }
                    }
                }
                values.add(builder.toString());
            }
        }
        try {
            printer.printRecord(values);
        } catch (IOException e) {
            throw new FhException("Error during Table Row to CSV export.", e);
        }
    }

}
