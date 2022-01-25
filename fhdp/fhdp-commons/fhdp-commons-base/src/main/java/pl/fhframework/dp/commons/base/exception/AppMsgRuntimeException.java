package pl.fhframework.dp.commons.base.exception;

/**
 * Passing message with runtime error
 * 
 * @author <a href="mailto:dariusz_skrudlik@javiko.pl">Dariusz Skrudlik</a>
 * @version :  $, :  $
 * @created 2019-06-19
 */
public class AppMsgRuntimeException extends RuntimeException implements IAppMsgException {

    public AppMsgRuntimeException(String message) {
        super(message);
    }
}
