package pl.fhframework.dp.transport.dto.operations;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/03/2021
 */
@Getter @Setter
public class OperationStepDtoQuery extends BaseDtoQuery {
    private Long docID;
    private String operationGUID;
    private String masterProcessId;
    private String processId;
    private String stepId;
}
