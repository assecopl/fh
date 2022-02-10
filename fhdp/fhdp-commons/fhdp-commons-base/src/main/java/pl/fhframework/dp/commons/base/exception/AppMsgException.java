package pl.fhframework.dp.commons.base.exception;

/**
 * Passing message with application error
 *
 * @author <a href="mailto:dariusz_skrudlik@javiko.pl">Dariusz Skrudlik</a>
 * @version :  $, :  $
 * @created 2019-06-19
 */
public class AppMsgException extends Exception implements IAppMsgException {

    public AppMsgException(String message) {
        super(message);
    }
}
