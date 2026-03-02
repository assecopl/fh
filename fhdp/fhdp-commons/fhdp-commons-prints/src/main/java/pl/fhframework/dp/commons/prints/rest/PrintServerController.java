package pl.fhframework.dp.commons.prints.rest;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import pl.fhframework.dp.commons.prints.CheckTemplateRequestType;
import pl.fhframework.dp.commons.prints.CheckTemplateResponseType;
import pl.fhframework.dp.commons.prints.ComponentVersionRequestType;
import pl.fhframework.dp.commons.prints.ComponentVersionResponseType;
import pl.fhframework.dp.commons.prints.printservice.JasperService;
import pl.fhframework.dp.commons.prints.printservice.PrintException;
import pl.fhframework.dp.transport.prints.PrintRequestType;
import pl.fhframework.dp.transport.prints.PrintResponseType;
import pl.fhframework.dp.transport.prints.ReportParamType;
import pl.fhframework.dp.transport.prints.ResultType;

@Controller
@RequestMapping(value = "prints")
public class PrintServerController {

	
	@Autowired
	JasperService jasperService;

	Logger logger = LoggerFactory.getLogger(PrintServerController.class);
	
    @GetMapping("/heartbeat")
    @ResponseBody
    public String heartbeat(@RequestParam(name="name", required=false, defaultValue="Stranger") String name) {
        return "Print Server v1.0";
    }	
	
    @PostMapping("/print")
    @ResponseBody
	public PrintResponseType print(@RequestBody PrintRequestType printRequest) {
		try {
			return jasperService.print(printRequest);
		} catch (PrintException e) {
			logger.error("Błąd wydruku. {}", e.getMessage(), e);
			PrintResponseType printResponse = new PrintResponseType();
			printResponse.setResult(new ResultType());
			printResponse.getResult().setCode(e.getErrorCode());
			printResponse.getResult().setDescription(e.getMessage());
			return printResponse;
		} catch (JRException e) {
			logger.error("Błąd wydruku. {}", e.getMessage(), e);
			PrintResponseType printResponse = new PrintResponseType();
			printResponse.setResult(new ResultType());
			printResponse.getResult().setCode(-99);
			printResponse.getResult().setDescription(e.getMessage());
			return printResponse;
		} catch (Throwable e) {
			logger.error("Błąd wydruku. {}", e.getMessage(), e);
			PrintResponseType printResponse = new PrintResponseType();
			printResponse.setResult(new ResultType());
			printResponse.getResult().setCode(-99);
			printResponse.getResult().setDescription(e.getMessage());
			return printResponse;
		}
	}

	public ComponentVersionResponseType componentVersion(ComponentVersionRequestType componentVersionRequest) {
		return null;
	}


	public CheckTemplateResponseType checkTemplate(CheckTemplateRequestType checkTemplateRequest) {
		CheckTemplateResponseType result = new CheckTemplateResponseType();
		result.setResult(new ResultType());
		result.getResult().setCode(-100);
		
        try {

        	ByteArrayInputStream bis = new ByteArrayInputStream(checkTemplateRequest.getTemplate());
            JasperReport report = JasperCompileManager.compileReport(bis);
            result.setReportName(report.getName());
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            JasperCompileManager.writeReportToXmlStream(report, outputStream);
//            ByteArrayOutputStream compiledStream = new ByteArrayOutputStream();
//            JasperCompileManager.compileReportToStream(new ByteArrayInputStream(outputStream.toByteArray()), compiledStream);
//            pdrReport.setReportContent(compiledStream.toByteArray());



            for (JRParameter parameter : report.getParameters()) {
                if (!parameter.isSystemDefined()) {
                    ReportParamType reportParam = new ReportParamType();
                    reportParam.setName(parameter.getName());
                    reportParam.setDescription(parameter.getDescription());
                    reportParam.setType(parameter.getValueClassName());
                    result.getParam().add(reportParam);
                }
            }
            
            result.getResult().setCode(1);
            result.getResult().setDescription("OK");
            
        } catch (JRException e) {
            e.printStackTrace();
            result.getResult().setCode(-99);
            result.getResult().setDescription(e.getMessage());
        }		
		
		return result;
	}	
	
	
	
}
