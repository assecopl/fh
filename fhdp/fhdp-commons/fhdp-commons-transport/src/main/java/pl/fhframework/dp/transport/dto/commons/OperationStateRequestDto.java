package pl.fhframework.dp.transport.dto.commons;

import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/05/2020
 */
@Getter @Setter
public class OperationStateRequestDto {
    private Long docId;
    private String operationId;
    private String processId;
}
