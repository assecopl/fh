package pl.fhframework.dp.transport.dto.commons;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 04.09.2019
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodelistRequestDto implements Serializable {
    private String refDataCode;
    private String text;
    private String code;
    private LocalDate onDate;
    private String docType;
    private HashMap<String, String> parm; 
}
