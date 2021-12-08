package pl.fhframework.dp.commons.jaxb;

import java.io.Serializable;

public class JAXBException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    public JAXBException(String msg) {
        super(msg);
    }

    public JAXBException(String msg, Throwable cause) {
        super(msg,cause);
    }

    public JAXBException(Throwable cause) {

    }
}
