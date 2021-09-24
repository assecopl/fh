package pl.fhframework.core.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.DeflateStreamCompression;
import net.sf.jasperreports.engine.util.JRConcurrentSwapFile;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.reports.datasource.FhReportsFillSource;
import pl.fhframework.core.reports.datasource.FhReportsObjectDataSource;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.SessionManager;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.io.FileService;
import pl.fhframework.io.TemporaryResource;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;

/**
 * Created by pawel.ruta on 2017-11-13.
 */
@Service
public class JrReportUtils {
    @Autowired
    private FileService fileService;

    @Autowired
    private FhConversionService conversionService;

    @PostConstruct
    private void initJasper() {
        JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
        JRPropertiesUtil.getInstance(jasperReportsContext).setProperty("net.sf.jasperreports.default.pdf.encoding", "ISO-8859-2");
        JRPropertiesUtil.getInstance(jasperReportsContext).setProperty("net.sf.jasperreports.extension.registry.factory.fonts", "net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory");
        JRPropertiesUtil.getInstance(jasperReportsContext).setProperty("net.sf.jasperreports.extension.registry.factory.simple.font.families", "net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory");
    }

    private JRConcurrentSwapFile getJrSwapFile() {
        try {
            return new JRConcurrentSwapFile(fileService.getTempDirectoryInstance().toFile().getAbsolutePath(), 1024, 1024);
        } catch (Exception e) {
            throw new FhReportsException("Can't initialize virtualizer for report engine", e);
        }
    }

    protected JasperReport getJasperReport(Class reportClass) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String moduleId = ModuleRegistry.getModuleId(reportClass);
        Subsystem subsystem = ModuleRegistry.getByName(moduleId);

        String reportPath = String.format("%s.jasper", reportClass.getName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
        try {
            FhResource reportResource = FhResource.get(
                    resolver.getResource(FileUtils.resolve(subsystem.getBaseClassPath(), reportPath).toExternalForm())
            );
            return (JasperReport) JRLoader.loadObject(reportResource.getURL());
        } catch (Exception e) {
            throw new FhReportsException("Can't load compiled report", e);
        }
    }

    public JasperReport getJasperReport(String raportId) {
        Class raportClass = ReflectionUtils.getClassForName(raportId);
        return getJasperReport(raportClass);
    }

    public FhReportsFillSource dataSourceOf(Object reportModel, Iterator reportIter) {
        return new FhReportsObjectDataSource(reportModel, reportIter);
    }

    public FhReportsFillSource dataSourceOf(Object reportModel, String listIteratorAttr) {
        Object iterator = null;

        if (!StringUtils.isNullOrEmpty(listIteratorAttr)) {
            iterator = getFieldValue(reportModel, listIteratorAttr);
            if (iterator != null) {
                if (iterator instanceof Iterable) {
                    iterator = ((Iterable) iterator).iterator();
                } else if (!(iterator instanceof Iterator)) {
                    throw new FhReportsException(String.format("Incorrect iterator '%s' for report data source", listIteratorAttr));
                }
            }
        }

        return new FhReportsObjectDataSource(reportModel, (Iterator) iterator);
    }

    public Object getFieldValue(Object object, String field) {
        try {
            return ReflectionUtils.findGetter(object.getClass(), object.getClass().getDeclaredField(field)).get().invoke(object);
        } catch (Exception e) {
            throw new FhReportsException(String.format("Error while retrieving field '%s'", field), e);
        }
    }

    public Object getFieldValueStr(Object object, String field) {
        try {
            return formatValue(getFieldValue(object, field));
        } catch (Exception e) {
            throw new FhReportsException(String.format("Error while retrieving field '%s'", field), e);
        }
    }

    public Resource exportReportToPdfFile(String raportId, FhReportsFillSource dataSource) {
        return exportReport(raportId, dataSource, this::exportToPdf, ".pdf");
    }

    public Resource exportReportToXlsFile(String raportId, FhReportsFillSource dataSource) {
        return exportReport(raportId, dataSource, this::exportToXls, ".xls");
    }

    public Resource exportReportToXlsxFile(String raportId, FhReportsFillSource dataSource) {
        return exportReport(raportId, dataSource, this::exportToXlsx, ".xlsx");
    }

    protected Resource exportReport(String raportId, FhReportsFillSource dataSource, BiConsumer<JasperPrint, String> exportFunction, String extension) {
        Class raportClass = ReflectionUtils.getClassForName(raportId);
        JasperReport jasperReport = getJasperReport(raportId);

        return exportReport(raportClass, raportId, jasperReport, dataSource, exportFunction, extension);
    }

    protected Resource exportReport(Class raportClass, String raportId, JasperReport jasperReport, FhReportsFillSource dataSource, BiConsumer<JasperPrint, String> exportFunction, String extension) {
        DynamicClassName raportName = DynamicClassName.forClassName(raportId);
        Map<String, Object> parameters = dataSource.getValues();

        JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(1024, getJrSwapFile(), true, new DeflateStreamCompression(7));
        JRVirtualizationHelper.setThreadVirtualizer(virtualizer);
        parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

        try {
            LocalJasperReportsContext localJasperReportsContext = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
            localJasperReportsContext.setFileResolver(fileName -> {
                try {
                    String subsystem = (String) ReflectionUtils.findGetter(raportClass, "subsystemName", Optional.of(String.class)).get().invoke(null);
                    FhResource fhResource = ModuleRegistry.getByName(subsystem).getBasePath();
                    FhResource absolutePath = fhResource.resolve(raportClass.getPackage().getName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)));
                    FhResource fileResource = absolutePath.resolve(fileName);
                    return fileResource.getExternalPath().toFile();
                } catch (Exception e) {
                    throw new FhReportsException("Can't find resource for report", e);
                }
            });

            JasperPrint jasperPrint = JasperFillManager.getInstance(localJasperReportsContext).fill(jasperReport, parameters, dataSource);

            virtualizer.setReadOnly(true);

            Path pathToReport = fileService.generateHolder(raportName.getBaseClassName() + extension, SessionManager.getUserSession());

            exportFunction.accept(jasperPrint, pathToReport.toFile().getAbsolutePath());

            return new TemporaryResource(pathToReport.toFile());
        } catch (Exception e) {
            throw new FhReportsException("Can't export report", e);
        } finally {
            virtualizer.cleanup();
            JRVirtualizationHelper.clearThreadVirtualizer();
        }
    }

    private void exportToXls(JasperPrint jasperPrint, String path) {
        JRXlsExporter exporter = new JRXlsExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(path)));

        /*SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        exporter.setConfiguration(configuration);*/

        try {
            exporter.exportReport();
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportToXlsx(JasperPrint jasperPrint, String path) {
        JRXlsxExporter exporter = new JRXlsxExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(path)));

        try {
            exporter.exportReport();
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private void exportToPdf(JasperPrint jasperPrint, String path) {
        try {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatValue(Object value) {
        return conversionService.convert(value, String.class);
    }
}
