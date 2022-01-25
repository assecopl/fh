package pl.fhframework.dp.transport.searchtemplate;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import pl.fhframework.dp.commons.base.model.IPersistentLong;

import java.io.Serializable;
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
}
