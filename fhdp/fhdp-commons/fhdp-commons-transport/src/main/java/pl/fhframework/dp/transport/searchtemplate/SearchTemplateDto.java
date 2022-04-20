package pl.fhframework.dp.transport.searchtemplate;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import pl.fhframework.dp.commons.base.model.IPersistentLong;
import pl.fhframework.dp.transport.converters.CustomZonedDateTimeConverter;

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
    @ValueConverter(CustomZonedDateTimeConverter.class)
    private LocalDateTime created;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
    @ValueConverter(CustomZonedDateTimeConverter.class)
    private LocalDateTime modified;

    private String lastUserName;
    private String description;
}
