package pl.fhframework.dp.commons.rest;


import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.fhframework.dp.transport.dto.commons.OperationDto;

public class OperationDataRestResponse<O extends OperationDto> extends BaseRestResponse<O, O> {

    public OperationDataRestResponse() {
    }

    @JsonIgnore
    public O getOperationDataDto() {
        return getRestObject().getData();
    }

    public void setOperationDataDto(O operationResultDto) {
        getRestObject().setData(operationResultDto);
    }

}
