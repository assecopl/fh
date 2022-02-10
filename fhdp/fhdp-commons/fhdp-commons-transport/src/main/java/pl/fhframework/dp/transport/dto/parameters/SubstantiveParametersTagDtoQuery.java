package pl.fhframework.dp.transport.dto.parameters;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;

@Getter
@Setter
public class SubstantiveParametersTagDtoQuery extends BaseDtoQuery {
  private String name;
}
