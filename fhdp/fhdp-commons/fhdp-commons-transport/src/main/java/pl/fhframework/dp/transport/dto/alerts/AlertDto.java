package pl.fhframework.dp.transport.dto.alerts;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import pl.fhframework.dp.commons.base.model.IPersistentObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/03/2021
 */
@Document(indexName = "#{@indexNamePrefix}_alert")
@Setting(settingPath = "/settings/settings.json")
@Getter
@Setter
public class AlertDto implements IPersistentObject<String> {
    @Id
    private String id;
    private AlertCodeEnum code;
    private String name;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS[X]")
    private LocalDateTime time;
    private String office;
    private Long declarationId;
    private String description;
    private String guidelines;
    private List<String> roles;
    private String kindOfDeclaration;
    private String lrn;
    private String mrn;

    public AlertDto() {
        id = UUID.randomUUID().toString();
        time = LocalDateTime.now();
    }
}
