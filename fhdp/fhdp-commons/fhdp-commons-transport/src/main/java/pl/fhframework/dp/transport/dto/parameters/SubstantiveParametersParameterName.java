package pl.fhframework.dp.transport.dto.parameters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubstantiveParametersParameterName {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    private String language;
    private String name;
}
