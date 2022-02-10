package pl.fhframework.dp.commons.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.OperationDto;

@Getter @Setter
public class OperationRestRequest<O extends OperationDto> extends BaseRestRequest<O> {

    public OperationRestRequest() {
    }

    String operationDtoServiceName;


    @JsonIgnore
    public O getOperationDto() {
        return getRestObject().getData();
    }

    public void setOperationDto(O operationDto) {
        getRestObject().setData(operationDto);
    }

}
