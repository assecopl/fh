package pl.fhframework.dp.transport.service;


import pl.fhframework.dp.transport.dto.commons.OperationDto;
import pl.fhframework.dp.transport.dto.commons.OperationResultBaseDto;
import pl.fhframework.dp.transport.dto.commons.OperationStateRequestDto;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;

import java.util.HashMap;


public interface IOperationDtoService<O extends OperationDto, R extends OperationResultBaseDto> {

    /**
     * Performs operation.
     *
     * On the client side, beanConversionUtil must be used, because in case of error,
     * OperationResultBaseDto is returned. See GetMessageOperationHandler.performOperation()
     *
     * @param operationDto
     * @return
     */
    R performOperation(O operationDto);

    default O getOperationData(Long id, HashMap<String, String> params) {
        return null;
    }

    default OperationStateResponseDto getOperationState(OperationStateRequestDto request){
        return null;
    }
}
