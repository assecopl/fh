package pl.fhframework.dp.transport.dto.commons;

import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Klasta transportowa do wykonywania operacji
 *
 * @author <a href="mailto:dariusz_skrudlik@javiko.pl">Dariusz Skrudlik</a>
 * @version :  $, :  $
 * @created 2019-05-31
 */
@Getter
@Setter
public class OperationDto implements Serializable {

    private static final long serialVersionUID = 3858227599726894833L;
    protected Long sequenceNumber;
    protected String code;
    protected String performer;
    protected String office;
    protected LocalDateTime startTime;
    protected LocalDateTime finishTime;
    protected LocalDateTime formalTime;
    protected String annotation;
    protected String guid;
    protected String details;
    private boolean isStep = false;

    public OperationDto() {
        this.guid = UUID.randomUUID().toString();
    }

    public OperationDto(String operationCode, String performer, String annotation) {
        this.code = operationCode;
        this.performer = performer;
        this.annotation = annotation;
        if(guid == null) {
            this.guid = UUID.randomUUID().toString();
        }
    }

    public OperationDto(String operationCode, String performer) {
        this(operationCode, performer, null);
    }

    public OperationDto(String operationCode) {
        this(operationCode, "System");
    }

}
