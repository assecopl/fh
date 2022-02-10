package pl.fhframework.dp.transport.dto.parameters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubstantiveParametersParameterDescription {
    @Id
    private String id;
    private String language;
    private String description;
}
