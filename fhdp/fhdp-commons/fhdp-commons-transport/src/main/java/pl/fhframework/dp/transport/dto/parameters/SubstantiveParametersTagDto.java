package pl.fhframework.dp.transport.dto.parameters;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;
import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.transport.searchtemplate.ResourceBundlePrefix;

@Document(indexName = "#{@indexNamePrefix}_substantive_parameters_tag")
@Setting(settingPath = "/settings/settings.json")
@ResourceBundlePrefix("pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersTagDto")
@Getter @Setter
public class SubstantiveParametersTagDto implements IPersistentObject<String>, Cloneable {

  @Id
  @Field(type = FieldType.Keyword)
  private String id;

  String name;
}
