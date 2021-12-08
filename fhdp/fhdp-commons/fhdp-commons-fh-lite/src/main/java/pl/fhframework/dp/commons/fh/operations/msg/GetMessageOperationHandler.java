package pl.fhframework.dp.commons.fh.operations.msg;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.fh.utils.rest.facade.FacadeClientFactory;
import pl.fhframework.dp.commons.utils.conversion.BeanConversionUtil;
import pl.fhframework.dp.transport.dto.commons.GetMessageOperationResultBaseDto;
import pl.fhframework.dp.transport.dto.commons.OperationGetMessageDto;
import pl.fhframework.dp.transport.service.IGetMessageOperationService;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02/03/2021
 */
public class GetMessageOperationHandler implements IGetMessageOperationService {
    @Autowired
    FacadeClientFactory facadeClientFactory;

    @Override
    public GetMessageOperationResultBaseDto performOperation(OperationGetMessageDto operationDto) {
        IGetMessageOperationService proxy = facadeClientFactory.createOperationServiceProxy(IGetMessageOperationService.class);
        Object ret = proxy.performOperation(operationDto);
        //WARNING! In case of error, OperationResultBaseDto is returned, so it should be converted by json to be able to process error.
        GetMessageOperationResultBaseDto result = BeanConversionUtil.mapObject(ret, false, GetMessageOperationResultBaseDto.class);
        return result;
    }
}
