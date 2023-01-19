package pl.fhframework.dp.transport.changelog;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 */
@Getter @Setter
public class ChangeLogDtoQuery extends BaseDtoQuery {
    private String id;
    private LocalDateTime timeFrom;
    private LocalDateTime timeTo;
    private String description;
}
