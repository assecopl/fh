package pl.fhframework.dp.transport.dto.alerts;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;

import java.util.List;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/03/2021
 */
@Getter @Setter
public class AlertDtoQuery extends BaseDtoQuery {
    private AlertCodeEnum code;
    private String office;
    private Long declarationId;
    private List<String> roles;
}
