package pl.fhframework.dp.commons.services.listeners;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.services.constants.CommonProcessConstants;
import pl.fhframework.dp.commons.services.semaphore.SemaphoreService;
import pl.fhframework.dp.commons.services.semaphore.SemaphoreTypeEnum;

@Service
@Slf4j

public class SetStateExecutionListener implements ExecutionListener {

    @Autowired
    ISetStateService setStateService;

    @Autowired
    SemaphoreService semaphoreService;

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        String state = delegateExecution.getCurrentActivityId();
        Long id = (Long) delegateExecution.getVariable(CommonProcessConstants.DECLARATION_ID);
        setStateService.setState(state, id);
        String operationGuid = (String) delegateExecution.getVariable(CommonProcessConstants.OPERATION_GUID);
        if(operationGuid != null) {
            semaphoreService.unlockSemaphore(SemaphoreTypeEnum.declaration, String.valueOf(id), operationGuid);
        }
        delegateExecution.setVariable(CommonProcessConstants.OPERATION_GUID, null);
    }
}
