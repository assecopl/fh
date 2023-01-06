package pl.fhframework.dp.commons.fh.document.handling;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.fh.utils.rest.facade.FacadeClientFactory;
import pl.fhframework.dp.transport.dto.commons.OperationResultBaseDto;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;
import pl.fhframework.dp.transport.service.IOperationDtoService;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 25/05/2020
 */
@Service
@Getter @Setter
public abstract class BaseOperationHandler<UC extends BaseDocumentHandlingUC> {
    protected UC documentHandlingUC;

    @Autowired
    FacadeClientFactory facadeClientFactory;

    public abstract void performOperation();

    protected void returnOperationResult(OperationResultBaseDto resultDto) {
        documentHandlingUC.afterPerformOperation(resultDto);
    }

    public OperationStateResponseDto checkOperationState(String operationId, Long docId, String processId) {
        return null;
    }
}
