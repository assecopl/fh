package pl.fhframework.dp.commons.services.listeners;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.services.constants.CommonProcessConstants;
import pl.fhframework.dp.commons.services.semaphore.SemaphoreService;
import pl.fhframework.dp.commons.services.semaphore.SemaphoreTypeEnum;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02/06/2020
 */
@Service
@Slf4j
public class SetStateListener implements TaskListener {
    @Autowired
    ISetStateService setStateService;
    @Autowired
    SemaphoreService semaphoreService;

    @Override
    public void notify(DelegateTask delegateTask) {
        String state = delegateTask.getTaskDefinitionKey();
        Long id = (Long) delegateTask.getExecution().getVariable(CommonProcessConstants.DECLARATION_ID);
        setStateService.setState(state, id);
        String operationGuid = (String) delegateTask.getExecution().getVariable(CommonProcessConstants.OPERATION_GUID);
        if(operationGuid != null) {
            semaphoreService.unlockSemaphore(SemaphoreTypeEnum.declaration, String.valueOf(id), operationGuid);
        }
        delegateTask.getExecution().setVariable(CommonProcessConstants.OPERATION_GUID, null);
    }
}
