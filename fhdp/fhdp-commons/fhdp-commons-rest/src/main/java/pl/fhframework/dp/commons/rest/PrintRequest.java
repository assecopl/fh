package pl.fhframework.dp.commons.rest;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-04-02
 */
@Getter @Setter
public class PrintRequest {
    private String documentType;
    private String templateCode;
    private String contentXml;
}
