package pl.fhframework.dp.commons.prints.printservice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;

import javax.ejb.EJBContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.query.JsonQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.ExporterConfiguration;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.export.type.PdfaConformanceEnum;
import net.sf.jasperreports.repo.RepositoryService;
import pl.fhframework.dp.commons.prints.Constants;
import pl.fhframework.dp.commons.prints.extensions.ClasspathRepositoryService;
import pl.fhframework.dp.commons.prints.i18n.PrintServerMessages;

public class JasperReportUtil {

    private static final Logger logger = LoggerFactory.getLogger(JasperReportUtil.class);

    public static byte[] export(JasperPrint jasperPrint, ExportTypeEnum exportType) {
        return export(jasperPrint, exportType, null);
    }

    public static byte[] export(JasperPrint jasperPrint, ExportTypeEnum exportType, Map<String, Object> params) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            ExporterInput input = null;
            ExporterOutput output = null;
            Exporter exporter = null;
            ExporterConfiguration configuration = null;
            ReportExportConfiguration reportExportConfiguration = null;
            switch (exportType) {
                case HTML:
                    output = new SimpleHtmlExporterOutput(baos);
                    exporter = new HtmlExporter();
//                    exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, false);
//                    exporter.setParameter(JRHtmlExporterParameter.ZOOM_RATIO, zoom);
                    configuration = new SimpleHtmlExporterConfiguration();
                    break;
                case DOCX:
                    exporter = new JRDocxExporter();
                    break;
                case RTF:
                    exporter = new JRRtfExporter();
                    break;
                case XML:
                    exporter = new JRXmlExporter();
                    break;
                case XLSX:
                    exporter = new JRXlsxExporter();
                    reportExportConfiguration = new SimpleXlsxReportConfiguration();
                    ((SimpleXlsxReportConfiguration) reportExportConfiguration).setOnePagePerSheet(false);
                    ((SimpleXlsxReportConfiguration) reportExportConfiguration).setDetectCellType(true);
                    ((SimpleXlsxReportConfiguration) reportExportConfiguration).setWhitePageBackground(false);
                    ((SimpleXlsxReportConfiguration) reportExportConfiguration).setIgnoreGraphics(false);
                    break;
                case XLS:
                    exporter = new JRXlsExporter();
                    reportExportConfiguration = new SimpleXlsReportConfiguration();
                    ((SimpleXlsReportConfiguration) reportExportConfiguration).setOnePagePerSheet(false);
                    ((SimpleXlsReportConfiguration) reportExportConfiguration).setDetectCellType(true);
                    ((SimpleXlsReportConfiguration) reportExportConfiguration).setWhitePageBackground(false);
                    ((SimpleXlsReportConfiguration) reportExportConfiguration).setIgnoreGraphics(false);
                    break;
                case PDF:
                default:
                    JasperReportsContext ctx = null;
                    configuration = new SimplePdfExporterConfiguration();
//                    ((SimplePdfExporterConfiguration) configuration).setMetadataCreator(PrintServerMessages.get().getString(Constants.PDF_METADATA_CREATOR, JRConstants.VERSION_6_5_1));
                    ((SimplePdfExporterConfiguration) configuration).setDisplayMetadataTitle(true);
                    ((SimplePdfExporterConfiguration) configuration).setMetadataTitle(getConfiguredValue(params, Constants.PDF_METADATA_TITLE, PrintServerMessages.get().getString(Constants.PDF_METADATA_TITLE)));
                    ((SimplePdfExporterConfiguration) configuration).setMetadataSubject(getConfiguredValue(params, Constants.PDF_METADATA_SUBJECT, PrintServerMessages.get().getString(Constants.PDF_METADATA_SUBJECT)));
                    ((SimplePdfExporterConfiguration) configuration).setMetadataAuthor(getConfiguredValue(params, Constants.PDF_METADATA_AUTHOR, PrintServerMessages.get().getString(Constants.PDF_METADATA_AUTHOR)));
                    if (getConfiguredValue(params, Constants.PDF_TAG, true)) {
                        ((SimplePdfExporterConfiguration) configuration).setTagged(true);
                    }
                    ((SimplePdfExporterConfiguration) configuration).setTagLanguage(getConfiguredValue(params, Constants.PDF_TAG_LANGUAGE, "pl"));
                    PdfaConformanceEnum pdfaConformance = PdfaConformanceEnum.getByName(getConfiguredValue(params, Constants.PDF_CONFORMANCE, PdfaConformanceEnum.PDFA_1A.getName()));
                    if (pdfaConformance != null) {
                        ((SimplePdfExporterConfiguration) configuration).setPdfaConformance(pdfaConformance);
                        // with PDF/A-1 need set up icc profile
                        ((SimplePdfExporterConfiguration) configuration).setIccProfilePath("classpath:/icc/sRGB.icc");
                        // custom factory to load icc from classpath
                        ctx = new SimpleJasperReportsContext();
                        RepositoryService repositoryService = new ClasspathRepositoryService(DefaultJasperReportsContext.getInstance());
                        ((SimpleJasperReportsContext) ctx).setExtensions(RepositoryService.class, Arrays.asList(repositoryService));
                    }
                    exporter = ctx != null ? new JRPdfExporter(ctx) : new JRPdfExporter();
            }
            if (input == null) {
                input = new SimpleExporterInput(jasperPrint);
            }
            exporter.setExporterInput(input);
            if (output == null) {
                output = new SimpleOutputStreamExporterOutput(baos);
            }
            exporter.setExporterOutput(output);
            if (configuration != null) {
                exporter.setConfiguration(configuration);
            }
            if (reportExportConfiguration != null) {
                exporter.setConfiguration(reportExportConfiguration);
            }
            exporter.exportReport();
            baos.flush();
            baos.close();

        } catch (JRException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return baos.toByteArray();
    }

    private static String getConfiguredValue(Map<String, Object> params, String key, String def) {
        if (params != null
                && params.containsKey(key)
                && params.get(key) != null) {
            return params.get(key).toString();
        } else {
            return System.getProperty(key, def);
        }
    }

    private static boolean getConfiguredValue(Map<String, Object> params, String key, boolean def) {
        if (params != null
                && params.containsKey(key)
                && params.get(key) != null) {
            Object configuredValue = params.get(key);
            if (params.get(key) instanceof Boolean) {
                return (Boolean) configuredValue;
            } else {
                return "true".equalsIgnoreCase(params.get(key).toString())
                        || "t".equalsIgnoreCase(params.get(key).toString())
                        || "1".equalsIgnoreCase(params.get(key).toString());
            }
        } else {
            String property = System.getProperty(key, Boolean.toString(def).toLowerCase());
            return "true".equalsIgnoreCase(property) || "t".equalsIgnoreCase(property) || "t".equalsIgnoreCase(property);
        }
    }

    public static byte[] exportToXls(JasperPrint jasperPrint) {
        return exportToXls(jasperPrint, null);
    }

    public static byte[] exportToXls(JasperPrint jasperPrint, Map<String, Object> params) {
        return export(jasperPrint, ExportTypeEnum.XLS, params);
    }

    public static byte[] exportToXlsx(JasperPrint jasperPrint) {
        return exportToXlsx(jasperPrint, null);
    }

    public static byte[] exportToXlsx(JasperPrint jasperPrint, Map<String, Object> params) {
        return export(jasperPrint, ExportTypeEnum.XLSX, params);
    }

    public static byte[] exportToPDF(JasperPrint jasperPrint) {
        return exportToPDF(jasperPrint, null);
    }

    public static byte[] exportToPDF(JasperPrint jasperPrint, Map<String, Object> params) {
        return export(jasperPrint, ExportTypeEnum.PDF, params);
    }

    public static byte[] exportToDocx(JasperPrint jasperPrint) {
        return exportToDocx(jasperPrint, null);
    }

    public static byte[] exportToDocx(JasperPrint jasperPrint, Map<String, Object> params) {
        return export(jasperPrint, ExportTypeEnum.DOCX, params);
    }

    public static byte[] exportToHTML(JasperPrint jasperPrint) {
        return exportToHTML(jasperPrint, null);
    }

    public static byte[] exportToHTML(JasperPrint jasperPrint, Map<String, Object> params) {
        return export(jasperPrint, ExportTypeEnum.HTML, params);
    }

    public static byte[] serialize(JasperPrint jasperPrint) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(jasperPrint);
            oos.flush();
            oos.close();
            baos.flush();
            baos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return baos.toByteArray();
    }

    public static JasperPrint generateReport(byte[] template, String sourceType, byte[] xmlSource, Map<String, Object> params, byte[] bundleDefault, String localeLNG, String localeCountry) throws JRException, PrintException {
        Locale locale = new Locale("pl", "PL");
        Locale usLocale = new Locale("en", "US");

        if(localeLNG!=null) {
        	locale = new Locale(localeLNG, localeCountry);
        }
        
        if (sourceType == null || "XML".equals(sourceType.toUpperCase())) {

            Document document = null;
            String calcTemplateName = null;
            try {
                document = JRXmlUtils.parse(new ByteArrayInputStream(xmlSource), true);
                if (document != null
                        && document.getDocumentElement() != null
                        && document.getDocumentElement().getLocalName() != null) {
                    calcTemplateName = document.getDocumentElement().getLocalName().toUpperCase();
                }
            } catch (JRException e) {
                throw new RuntimeException("Error during XML parsing ", e);
            }
            //        InputStream reportStream = PrintServiceHelper.getTemplate(PrintEngineEnum.JASPER, StringUtils.isNotBlank(report) ? report : calcTemplateName);
            ByteArrayInputStream reportStream = new ByteArrayInputStream(template);
            Map<String, Object> jasperParms = new LinkedHashMap<String, Object>();
            jasperParms.put(JRXPathQueryExecuterFactory.PROPERTY_XML_NUMBER_PATTERN, "#0.00#");
            jasperParms.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#0.00#");
            jasperParms.put(JRXPathQueryExecuterFactory.PROPERTY_XML_DATE_PATTERN, "yyyy-MM-dd");
            jasperParms.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
            jasperParms.put(JRXPathQueryExecuterFactory.XML_LOCALE, usLocale);
            jasperParms.put(JRParameter.REPORT_LOCALE, locale);
            jasperParms.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
            // Dir for subreports to be set as report dir
            jasperParms.put("SUBREPORT_DIR", PrintEngineEnum.JASPER.getTemplateLocation());
            if (params != null) {
                for (String key : params.keySet()) {
                    jasperParms.put(key, params.get(key));
                }
            }
            
            if(bundleDefault!=null) {
            	ByteArrayInputStream bais = new ByteArrayInputStream(bundleDefault);
            	try {
            		PropertyResourceBundle resourceBundle = new PropertyResourceBundle(bais);
					jasperParms.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            // Customize Jasper Reports Context (add properties)
            SimpleJasperReportsContext ctx = new SimpleJasperReportsContext();
            System.getProperties().entrySet().stream()
                    .filter(property
                            -> property.getKey() instanceof String
                    && ((String) property.getKey()).startsWith("net.sf.jasperreports")
                    && property.getValue() instanceof String)
                    .forEach(property -> {
                        ctx.setProperty((String) property.getKey(), (String) property.getValue());
                    });
            // Compile report & fill manager with ctx
            JasperReport jasReport = JasperCompileManager.getInstance(ctx).compile(reportStream);
            JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
            JasperPrint jasperPrint = fillManager.fill(jasReport, jasperParms);
            return jasperPrint;
            
        } else if ("JSON".equals(sourceType.toUpperCase())) {          

            ByteArrayInputStream reportStream = new ByteArrayInputStream(template);
            Map<String, Object> jasperParms = new LinkedHashMap<String, Object>();
            jasperParms.put(JsonQueryExecuterFactory.JSON_NUMBER_PATTERN, "#0.00#");
            jasperParms.put(JsonQueryExecuterFactory.JSON_DATE_PATTERN, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            jasperParms.put(JsonQueryExecuterFactory.JSON_LOCALE, usLocale);
            jasperParms.put(JRParameter.REPORT_LOCALE, locale);
            jasperParms.put(JsonQueryExecuterFactory.JSON_INPUT_STREAM, new ByteArrayInputStream(xmlSource));
            // Dir for subreports to be set as report dir
            jasperParms.put("SUBREPORT_DIR", PrintEngineEnum.JASPER.getTemplateLocation());
            if (params != null) {
                for (String key : params.keySet()) {
                    jasperParms.put(key, params.get(key));
                }
            }
            
            if(bundleDefault!=null) {
            	ByteArrayInputStream bais = new ByteArrayInputStream(bundleDefault);
            	try {
            		PropertyResourceBundle resourceBundle = new PropertyResourceBundle(bais);
					jasperParms.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            // Customize Jasper Reports Context (add properties)
            SimpleJasperReportsContext ctx = new SimpleJasperReportsContext();
            System.getProperties().entrySet().stream()
                    .filter(property
                            -> property.getKey() instanceof String
                    && ((String) property.getKey()).startsWith("net.sf.jasperreports")
                    && property.getValue() instanceof String)
                    .forEach(property -> {
                        ctx.setProperty((String) property.getKey(), (String) property.getValue());
                    });
            // Compile report & fill manager with ctx
            JasperReport jasReport = JasperCompileManager.getInstance(ctx).compile(reportStream);
            JasperFillManager fillManager = JasperFillManager.getInstance(ctx);
            JasperPrint jasperPrint = fillManager.fill(jasReport, jasperParms);
            return jasperPrint;
          
        } else if ("JNDI".equals(sourceType.toUpperCase())) {
            String jndi = "java:/jdbc/DefaultDS";
            if (xmlSource != null && xmlSource.length > 0) {
                jndi = new String(xmlSource);
            }
//	        	DataSource ds = (DataSource) ejbContext.lookup(jndi);

            Connection connection;
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource) ctx.lookup(jndi);
                connection = ds.getConnection();
            } catch (Exception e) {
                throw new PrintException(e.getMessage());
            }
            //        InputStream reportStream = PrintServiceHelper.getTemplate(PrintEngineEnum.JASPER, StringUtils.isNotBlank(report) ? report : calcTemplateName);
            ByteArrayInputStream reportStream = new ByteArrayInputStream(template);
            Map<String, Object> jasperParms = new LinkedHashMap<>();
//		        jasperParms.put(JRXPathQueryExecuterFactory.PROPERTY_XML_NUMBER_PATTERN, "#0.00#");
//		        jasperParms.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#0.00#");
//		        jasperParms.put(JRXPathQueryExecuterFactory.PROPERTY_XML_DATE_PATTERN, "yyyy-MM-dd");
//		        jasperParms.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
//		        jasperParms.put(JRXPathQueryExecuterFactory.XML_LOCALE, usLocale);
//		        jasperParms.put(JRParameter.REPORT_LOCALE, locale);
//		        jasperParms.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
            // Dir for subreports to be set as report dir
            jasperParms.put("SUBREPORT_DIR", PrintEngineEnum.JASPER.getTemplateLocation());
            if (params != null) {
                for (String key : params.keySet()) {
                    jasperParms.put(key, params.get(key));
                }
            }

            // Customize Jasper Reports Context (add properties)
            JasperReportsContext ctx = DefaultJasperReportsContext.getInstance();
            System.getProperties().entrySet().stream()
                    .filter(property
                            -> property.getKey() instanceof String
                    && ((String) property.getKey()).startsWith("net.sf.jasperreports")
                    && property.getValue() instanceof String)
                    .forEach(property -> {
                        ctx.setProperty((String) property.getKey(), (String) property.getValue());
                    });
            JasperReport jasReport = JasperCompileManager.getInstance(ctx).compile(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasReport, jasperParms, connection);
            return jasperPrint;
        } else if ("JASPER".equals(sourceType.toUpperCase())) {

            ByteArrayInputStream bais = new ByteArrayInputStream(xmlSource);
            JasperPrint jasperPrint = null;

            try {
                ObjectInputStream ois = new ObjectInputStream(bais);
                jasperPrint = (JasperPrint) ois.readObject();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return jasperPrint;
        } else {
            throw new PrintException("Obecnie obsługiwane jest tylko źródło typu XML");
        }
    }
}
