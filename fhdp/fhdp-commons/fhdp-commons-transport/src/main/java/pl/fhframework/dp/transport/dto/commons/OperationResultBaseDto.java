package pl.fhframework.dp.transport.dto.commons;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class OperationResultBaseDto implements Serializable {

    private static final long serialVersionUID = 6470652337734973557L;

    private Long documentId;
    private String operationID;
    private String processID;
    private boolean ok = true;
    private Long resultCode; //1: Ok, 0: No action, <0: Error
    private String resultMessage;
    private boolean displayOperationResult = false;
}
