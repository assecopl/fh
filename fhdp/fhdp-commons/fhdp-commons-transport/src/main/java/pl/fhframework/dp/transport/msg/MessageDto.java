package pl.fhframework.dp.transport.msg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.transport.drs.repository.Metadata;
import pl.fhframework.dp.transport.enums.MessageDirectionEnum;
import pl.fhframework.dp.transport.searchtemplate.ResourceBundlePrefix;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 13/06/2020
 */
@Document(indexName = "#{@indexNamePrefix}_message")
@Setting(settingPath = "/settings/settings.json")
@ResourceBundlePrefix("pl.fhframework.dp.transport.msg.MessageDto")
@Getter @Setter
public class MessageDto implements IPersistentObject<String> {
    @Id
    private String repositoryId;
    private Long declarationId;
    private String name;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime stored = LocalDateTime.now();
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime delivered;
    private MessageDirectionEnum direction;
    @Field(type = FieldType.Nested)
    private Metadata metadata;
    private String content;

    @Override
    public String getId() {
        return repositoryId;
    }

    @Override
    public void setId(String id) {
        repositoryId = id;
    }
}
