package pl.fhframework.core.logging;

/**
 * Created by pawel.ruta on 2018-06-26.
 */
public interface IBusinessLogger {
    void log(Class clazz, BusinessLogLevel level, String message, Object... messageArguments);
}
