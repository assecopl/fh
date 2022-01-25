package pl.fhframework.dp.transport.auditlog;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;
import pl.fhframework.dp.transport.dto.document.SeverityEnum;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 15/09/2020
 */
@Getter @Setter
public class AuditLogDtoQuery extends BaseDtoQuery {
    private AuditLogTypeEnum type;
    private SeverityEnum severity;
    private String category;
    private LocalDateTime eventTimeFrom;
    private LocalDateTime eventTimeTo;
    private String messageKey;
    private String comment;
    private String processID;
    private String operationGUID;
    private String stepID;
    private String userLogin;
}
