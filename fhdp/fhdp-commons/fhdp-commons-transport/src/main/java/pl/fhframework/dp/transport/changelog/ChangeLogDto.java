package pl.fhframework.dp.transport.changelog;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.transport.converters.CustomZonedDateTimeConverter;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 */
@Document(indexName = "#{@indexNamePrefix}_change_log", createIndex = false)
@Setting(settingPath = "/settings/settings.json")
@Getter
@Setter
@NoArgsConstructor
public class ChangeLogDto implements IPersistentObject<String> {
    @Id
    private String id;
    private String description;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    @ValueConverter(CustomZonedDateTimeConverter.class)
    private LocalDateTime changeTime;

    public ChangeLogDto(String id, String description) {
        this.id = id;
        this.description = description;
        this.changeTime = LocalDateTime.now();
    }
}
