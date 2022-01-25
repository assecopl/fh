package pl.fhframework.dp.transport.dto.operations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import pl.fhframework.dp.commons.base.model.IPersistentObject;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/03/2021
 */
@Document(indexName = "#{@indexNamePrefix}_operation_step")
@Setting(settingPath = "/settings/settings.json")
@Getter
@Setter
public class OperationStepDto implements IPersistentObject<String> {
    @Id
    private String id;
    private Long docID;
    private String operationGUID;
    private String masterProcessId;
    private String processId;
    private String stepId;
    private String messageKey;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime started;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime finished;
}
