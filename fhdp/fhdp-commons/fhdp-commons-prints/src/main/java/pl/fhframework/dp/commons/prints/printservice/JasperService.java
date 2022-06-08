package pl.fhframework.dp.commons.prints.printservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import pl.fhframework.dp.transport.prints.FormatType;
import pl.fhframework.dp.transport.prints.ParameterType;
import pl.fhframework.dp.transport.prints.PrintRequestType;
import pl.fhframework.dp.transport.prints.PrintResponseType;
import pl.fhframework.dp.transport.prints.PrintoutType;
import pl.fhframework.dp.transport.prints.ResultType;



@Service
public class JasperService {


    Logger logger = LoggerFactory.getLogger(JasperService.class);

    public PrintResponseType print(PrintRequestType request) throws PrintException, JRException {
        PrintResponseType response = new PrintResponseType();
        response.setPrintout(new ArrayList<PrintoutType>());

        try {
            Map<String, Object> params = new HashMap<>();
            if (request.getParameter() != null) {
                for (ParameterType param : request.getParameter()) {
                    logger.debug("Parameter [" + param.getName() + "]: " + param.getValue());
                    if ("java.util.Date".equals(param.getType())) {
                        Date dt = DateHelper.stringToDate(param.getValue());
                        params.put(param.getName(), dt);
                    } else {
                        params.put(param.getName(), param.getValue());
                    }
                }
            }
            JasperPrint jPrint = JasperReportUtil.generateReport(request.getTemplate(), request.getSourceType(), request.getSource(), params, request.getBundleDefault(), request.getLocaleLanguage(), request.getLocaleCountry());
            List<FormatType> outputFormat = request.getOutputFormat() != null && !request.getOutputFormat().isEmpty() ? request.getOutputFormat() : Arrays.asList(FormatType.PDF);
            for (FormatType format : outputFormat) {
                PrintoutType printout = new PrintoutType();
                if (format != null) {
                    // CXF pass null if not valid FormatType
                    printout.setFormat(format);
                    switch (format) {

                        case XLS:
                            printout.setContent(JasperReportUtil.exportToXls(jPrint, params));
                            break;

                        case XLSX:
                            printout.setContent(JasperReportUtil.exportToXlsx(jPrint, params));
                            break;

                        case PDF:
                            printout.setContent(JasperReportUtil.exportToPDF(jPrint, params));
                            break;

                        case DOCX:
                            printout.setContent(JasperReportUtil.exportToDocx(jPrint, params));
                            break;

                        case HTML:
                            printout.setContent(JasperReportUtil.exportToHTML(jPrint, params));
                            break;

                        case JASPER:
                            printout.setContent(JasperReportUtil.serialize(jPrint));
                            break;

                        default:
                            throw new PrintException(PrintException.ERROR_UNSUPPORTED_OUTPUT_FORMAT);

                    }
                }
                if (printout.getContent() == null) {
                    throw new PrintException(-1);
                }
                response.getPrintout().add(printout);
            }

            response.setResult(new ResultType());
            response.getResult().setCode(1);
            return response;
        } catch (Throwable e) {
            logger.error("Błąd wydruku. {}", e.getMessage(), e);
            PrintResponseType printResponse = new PrintResponseType();
            printResponse.setResult(new ResultType());
            printResponse.getResult().setCode(-99);
            printResponse.getResult().setDescription(ExceptionUtils.getFullStackTrace(e));
           
            return printResponse;
        }
    }
}
