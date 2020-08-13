package pl.fhframework.core.logging;

/**
 * Created by Adam Zareba on 02.02.2017.
 */
public interface LoggerView {

    void log(String message, Object... messageArgumentsWithOptionalException);
}
