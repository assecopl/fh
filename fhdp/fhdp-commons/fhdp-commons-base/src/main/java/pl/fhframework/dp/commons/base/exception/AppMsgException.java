package pl.fhframework.dp.commons.base.exception;

public class AppMsgException extends Exception implements IAppMsgException {

    public AppMsgException(String message) {
        super(message);
    }
}
