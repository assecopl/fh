package pl.fhframework.core.logging;

import pl.fhframework.core.CoreSystemFunction;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.services.FhService;
import pl.fhframework.core.uc.Parameter;

/**
 * Created by pawel.ruta on 2018-06-27.
 */
@FhService(groupName = "logs", categories = "log")
@SystemFunction(CoreSystemFunction.CORE_LOGS)
public class FhLoggerService {
    public void info(@Parameter(name = "message") String message) {
        FhLogger.businessLog(BusinessLogLevel.INFO, message);
    }

    public void warn(@Parameter(name = "message") String message) {
        FhLogger.businessLog(BusinessLogLevel.WARN, message);
    }

    public void error(@Parameter(name = "message") String message) {
        FhLogger.businessLog(BusinessLogLevel.ERROR, message);
    }

    public void critical(@Parameter(name = "message") String message) {
        FhLogger.businessLog(BusinessLogLevel.CRITICAL, message);
    }

    public void rodo(@Parameter(name = "message") String message) {
        FhLogger.businessLog(BusinessLogLevel.RODO, message);
    }

    public void security(@Parameter(name = "message") String message) {
        FhLogger.businessLog(BusinessLogLevel.SECURITY, message);
    }

    public void sla(@Parameter(name = "message") String message) {
        FhLogger.businessLog(BusinessLogLevel.SLA, message);
    }
}
