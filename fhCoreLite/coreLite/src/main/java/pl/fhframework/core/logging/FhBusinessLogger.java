package pl.fhframework.core.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.ReflectionUtils;

import javax.annotation.PostConstruct;

/**
 * Created by pawel.ruta on 2018-06-26.
 */
@Service
public class FhBusinessLogger {

    private static IBusinessLogger LOGGER;

    private static IBusinessLogger FAILOVER_LOGGER = (clazz, level, message, messageArguments) ->
            FhLogger.log(LogLevel.INFO, String.format("%s / '%s' - %s", level, clazz == null ? "" : ReflectionUtils.getClassName(clazz), message), messageArguments);

    @Autowired(required = false)
    private IBusinessLogger businessLogger;

    public static IBusinessLogger getLogger() {
        if (LOGGER != null) {
            return LOGGER;
        } else {
            return FAILOVER_LOGGER;
        }
    }

    public static IBusinessLogger getFailoverLogger() {
        return FAILOVER_LOGGER;
    }

    @PostConstruct
    private void init() {
        if (businessLogger != null) {
            LOGGER = businessLogger;
        }
    }
}
