package pl.fhframework.dp.transport.searchtemplate;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import pl.fhframework.dp.commons.base.model.IPersistentLong;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Document(indexName = "#{@indexNamePrefix}_search_template")
@Setting(settingPath = "/settings/settings.json")
@Getter@Setter
public class SearchTemplateDto implements Serializable, IPersistentLong {
    @Id
    private Long id;
    private Long version;
    private String templateName;
    private String userName;
    private String componentName;
    private List<SearchTemplateDefinition> searchTemplateDefinitions;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime created;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime modified;

    private String lastUserName;
    private String description;
}
