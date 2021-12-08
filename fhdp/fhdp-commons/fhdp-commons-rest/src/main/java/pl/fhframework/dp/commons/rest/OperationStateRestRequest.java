package pl.fhframework.dp.commons.rest;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.OperationStateRequestDto;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/05/2020
 */
@Getter @Setter
public class OperationStateRestRequest extends BaseRestRequest<OperationStateRequestDto> {
    private String operationDtoServiceName;
}
