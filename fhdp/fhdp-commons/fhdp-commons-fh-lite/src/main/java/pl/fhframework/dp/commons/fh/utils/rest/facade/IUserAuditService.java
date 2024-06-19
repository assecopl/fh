package pl.fhframework.dp.commons.fh.utils.rest.facade;

import pl.fhframework.dp.transport.dto.commons.OperationDto;
import pl.fhframework.dp.transport.dto.commons.OperationResultBaseDto;

public interface IUserAuditService {
    void registerOperationSuccess(String operationName, OperationDto operationDto, OperationResultBaseDto responseDto, long duration);

    void registerOperationFailure(String operationName, OperationDto operationDto, OperationResultBaseDto errorResponseDto, long duration);
}
