package pl.fhframework.dp.commons.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter @Setter
public class OperationDataRestRequest extends BaseRestRequest<Long> {

    public OperationDataRestRequest() {
    }

    HashMap<String, String> paramsMap = new HashMap<>();
    String operationDtoServiceName;

}
