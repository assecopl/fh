package pl.fhframework.dp.transport.msg;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;

@Getter
@Setter
public class DeclarationMessageDtoQuery extends BaseDtoQuery {
    private Long declarationId; // id deklaracji dla której mają być pobrane komunikaty
    private boolean withContent; // czy komunikaty mają być pobrane razem z ich treścią w xml
}
