package pl.fhframework.dp.commons.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/05/2020
 */
public class OperationStateRestResponse extends BaseRestResponse<OperationStateResponseDto, OperationStateResponseDto> {

    @JsonIgnore
    public OperationStateResponseDto getOperationStateResponseDto() {
        return getRestObject().getData();
    }
}
