package pl.fhframework.dp.commons.fh.declaration.handling;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.fh.utils.rest.facade.FacadeClientFactory;
import pl.fhframework.dp.transport.dto.commons.OperationResultBaseDto;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 25/05/2020
 */
@Service
@Getter @Setter
public abstract class BaseOperationHandler<UC extends BaseDeclarationHandlingUC> {
    protected UC declarationHandlingUC;

    @Autowired
    FacadeClientFactory facadeClientFactory;

    public abstract void performOperation();

    protected void returnOperationResult(OperationResultBaseDto resultDto) {
        declarationHandlingUC.afterPerformOperation(resultDto);
    }

    public OperationStateResponseDto checkOperationState(String operationId, Long docId, String processId) {
        return null;
    }
}
