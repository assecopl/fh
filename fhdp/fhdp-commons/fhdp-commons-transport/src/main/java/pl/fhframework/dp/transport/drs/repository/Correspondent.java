package pl.fhframework.dp.transport.drs.repository;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 10/06/2020
 */
@Getter @Setter
public class Correspondent {
    private String domain;
    private String identifier;
    private String identifierType;

}
