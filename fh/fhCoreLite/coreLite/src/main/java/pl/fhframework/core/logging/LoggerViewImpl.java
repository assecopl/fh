package pl.fhframework.core.logging;

/**
 * Created by Adam Zareba on 03.02.2017.
 */
public class LoggerViewImpl implements LoggerView {

    private LogLevel level;

    private String className;

    public LoggerViewImpl(LogLevel level, String className) {
        this.level = level;
        this.className = className;
    }

    @Override
    public void log(String message, Object... messageArgumentsWithOptionalException) {
        FhLogger.logImpl(className, level, message, messageArgumentsWithOptionalException, null);
    }
}
