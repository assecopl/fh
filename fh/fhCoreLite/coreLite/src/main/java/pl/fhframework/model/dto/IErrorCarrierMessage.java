package pl.fhframework.model.dto;

import pl.fhframework.core.logging.ErrorInformation;

import java.util.List;

/**
 * Output communication message which may contain error information being sent to client.
 */
public interface IErrorCarrierMessage {

    void setErrors(List<ErrorInformation> errorInformation);
}
