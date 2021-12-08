package pl.fhframework.dp.transport.msg;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 05/06/2020
 */
@Getter @Setter
public class AcceptMessageRequest {
    private String msgRepositoryId;
    private String messageName;
    private boolean force = false;
}
