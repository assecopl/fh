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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Klasa odpowiedzialna za parametry merytoryczne.
 */
@Document(indexName = "#{@indexNamePrefix}_substantive_parameters")
@Setting(settingPath = "/settings/settings.json")
@ResourceBundlePrefix("pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDto")
@Getter
@Setter
public class SubstantiveParametersDto implements IPersistentObject<String>, Cloneable {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    private String components;
    private String key;
    private String value;
    private List<SubstantiveParametersParameterItem> values;
    private SubstantiveParametersValueTypeEnum valueTypes;
    private String pattern;
    private String maskDefinition;
    private String mask;
    private String scope;

    private List<SubstantiveParametersParameterName> parameterNames;
    private List<SubstantiveParametersParameterDescription> parameterDescriptions;
    private List<String> tags;
    private String tagsDisplay;
    private SubstantiveParametersUnit unit;

    private boolean isPerOffice;
    private String customsOffice;

    @Override
    public SubstantiveParametersDto clone() throws CloneNotSupportedException {
        SubstantiveParametersDto substantiveParametersDto = (SubstantiveParametersDto) super.clone();
        if (values != null) {
            substantiveParametersDto.setValues(new ArrayList(values.stream().map(item -> new SubstantiveParametersParameterItem(item.getValue())).collect(Collectors.toList())));
        }

        substantiveParametersDto.setTags(new ArrayList(Optional.ofNullable(tags)
                                                 .map(List::stream)
                                                 .orElseGet(Stream::empty)
                                                 .collect(Collectors.toList())));
        return substantiveParametersDto;
    }

    /**
     * Required to show merged values in a list view
     * @return
     */
    public List<String> getFormattedCollectionValues() {
        if (values != null) {
            return values.stream().map(item -> item.getValue()).collect(Collectors.toList());
        }
        return null;
    }
}
