package pl.fhframework.dp.transport.dto.commons;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.drs.repository.Document;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02/03/2021
 */
@Getter @Setter
public class GetMessageOperationResultBaseDto extends OperationResultBaseDto {
    Document document;
}
