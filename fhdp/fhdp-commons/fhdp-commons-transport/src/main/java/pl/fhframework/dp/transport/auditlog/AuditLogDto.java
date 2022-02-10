package pl.fhframework.dp.transport.auditlog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.transport.dto.document.SeverityEnum;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 15/09/2020
 */
@Document(indexName = "#{@indexNamePrefix}_audit_log", createIndex = false)
@Setting(settingPath = "/settings/settings.json")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogDto implements IPersistentObject<String> {
    @Id
    private String id;
    private AuditLogTypeEnum type;
    private SeverityEnum severity;
    private String category;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime eventTime;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime endTime;
    private String messageKey;
    private String comment;
    private String stepID;
    private String processID;
    private String operationGUID;
    private String userLogin;
    private Long docId;

    public AuditLogDto(AuditLogTypeEnum type,
                       SeverityEnum severity,
                       String category,
                       LocalDateTime eventTime,
                       String messageKey,
                       String comment,
                       String processID,
                       String operationGUID,
                       String stepID,
                       String userLogin) {
        this.type = type;
        this.severity = severity;
        this.category = category;
        this.eventTime = eventTime;
        this.messageKey = messageKey;
        this.comment = comment;
        this.processID = processID;
        this.stepID = stepID;
        this.operationGUID = operationGUID;
        this.userLogin = userLogin;
    }

}
