package pl.fhframework.dp.transport.dto.commons;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.*;
import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.transport.dto.document.SeverityEnum;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/05/2020
 */
@Document(indexName = "#{@indexNamePrefix}_operation_step")
@Setting(settingPath = "/settings/settings.json")
@Getter @Setter
public class OperationStepDto  implements Comparable<OperationStepDto>, IPersistentObject<String> {
    @Id
    private String id;
    private String description;
    private SeverityEnum type;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime started;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime finished;
    private Long docID;
    private String operationGUID;
    private String masterProcessId;
    private String processId;
    private String stepId;
    @Transient
    private float duration;

    @Override
    public int compareTo(OperationStepDto o) {
        if(this.started.equals(o.started)) return 0;
        return (this.started.isAfter(o.started))?-1:1;
    }
}
