package pl.fhframework.dp.commons.base.exception;

import java.io.Serializable;

public class AppException extends RuntimeException implements Serializable {

private static final long serialVersionUID = 1L;
    
	public AppException(String msg) {
		super(msg);
	}
        
        public AppException(String msg, Throwable cause) {
		super(msg,cause);
	}

}
