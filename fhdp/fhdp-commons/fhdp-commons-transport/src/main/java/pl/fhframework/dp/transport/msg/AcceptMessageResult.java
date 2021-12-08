package pl.fhframework.dp.transport.msg;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.Result;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 05/06/2020
 */
@Getter @Setter
public class AcceptMessageResult {
    private String acceptGuid;
    private boolean ok;
    private Result result;
    private byte[] responseBytes;
}
