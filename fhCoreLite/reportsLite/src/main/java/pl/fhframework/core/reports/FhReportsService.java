package pl.fhframework.core.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import pl.fhframework.core.reports.datasource.FhReportsObjectDataSource;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.services.FhService;
import pl.fhframework.event.EventRegistry;

import java.util.Iterator;

/**
 * Created by pawel.ruta on 2017-12-11.
 */
@FhService(groupName = "fhReports", categories = "reports")
@SystemFunction(ReportsSystemFunction.REPORTS_SERVICES_GENERATE)
public class FhReportsService {
    @Autowired
    JrReportUtils jrReportUtils;

    @Autowired
    private EventRegistry eventRegistry;

    public void generatePdfReport(String reportId, Object reportModel, Iterator reportList) {
        Resource pdfReport = jrReportUtils.exportReportToPdfFile(reportId, new FhReportsObjectDataSource(reportModel, reportList));

        eventRegistry.fireDownloadEvent(pdfReport);
    }

    public void generateXlsReport(String reportId, Object reportModel, Iterator reportList) {
        Resource xlsReport = jrReportUtils.exportReportToXlsFile(reportId, new FhReportsObjectDataSource(reportModel, reportList));

        eventRegistry.fireDownloadEvent(xlsReport);
    }

    public void generateXlsxReport(String reportId, Object reportModel, Iterator reportList) {
        Resource xlsReport = jrReportUtils.exportReportToXlsxFile(reportId, new FhReportsObjectDataSource(reportModel, reportList));

        eventRegistry.fireDownloadEvent(xlsReport);
    }
}
