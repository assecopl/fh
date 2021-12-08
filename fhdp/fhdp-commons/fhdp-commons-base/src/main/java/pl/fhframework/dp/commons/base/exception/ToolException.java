package pl.fhframework.dp.commons.base.exception;

public class ToolException extends AppException {
    
    private static final long serialVersionUID = 1L;
    
    public ToolException(String msg) {
        super(msg);
    }
    
    public ToolException(String msg, Throwable cause) {
        super(msg,cause);
    }
    
}
