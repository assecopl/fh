package pl.fhframework.dp.transport.dto.parameters;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;

@NoArgsConstructor
@Getter
@Setter
public class SubstantiveParametersDtoQuery extends BaseDtoQuery {
    private String name;
    private String key;
    private String tag;
    private String customsOffice;
    private Boolean isPerOffice;

    public SubstantiveParametersDtoQuery(String key, String customsOffice, Boolean isPerOffice) {
        this.key = key;
        this.customsOffice = customsOffice;
        this.isPerOffice = isPerOffice;
    }
}
