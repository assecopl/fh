package pl.fhframework.dp.commons.base.exception;

public class AppMsgRuntimeException extends RuntimeException implements IAppMsgException {

    public AppMsgRuntimeException(String message) {
        super(message);
    }
}
