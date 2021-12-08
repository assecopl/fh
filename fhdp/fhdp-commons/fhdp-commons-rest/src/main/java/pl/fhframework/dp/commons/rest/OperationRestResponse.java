package pl.fhframework.dp.commons.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.fhframework.dp.transport.dto.commons.OperationResultBaseDto;


public class OperationRestResponse<O extends OperationResultBaseDto> extends BaseRestResponse<O, O> {

    public OperationRestResponse() {
    }


    @JsonIgnore
    public O getOperationResultDto() {
        return getRestObject().getData();
    }

    public void setOperationResultDto(O operationResultDto) {
        getRestObject().setData(operationResultDto);
    }

}
