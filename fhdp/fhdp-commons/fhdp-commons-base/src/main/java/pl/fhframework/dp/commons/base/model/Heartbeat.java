package pl.fhframework.dp.commons.base.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-07-25
 */
@Getter @Setter
public class Heartbeat {
    private final long id;
    private final String message;
    private final String version;
    private LocalDateTime callTime = LocalDateTime.now();

    public Heartbeat(long id, String message, String version) {
        this.id = id;
        this.message = message;
        this.version = version;
    }
}
