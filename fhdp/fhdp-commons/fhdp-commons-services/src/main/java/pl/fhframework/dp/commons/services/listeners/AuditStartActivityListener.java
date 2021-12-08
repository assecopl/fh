package pl.fhframework.dp.commons.services.listeners;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.services.auditlog.AuditLogDtoService;
import pl.fhframework.dp.commons.services.constants.CommonProcessConstants;
import pl.fhframework.dp.commons.utils.xml.TextUtils;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02/06/2020
 */
@Service
@Slf4j
public class AuditStartActivityListener implements ExecutionListener {
    @Autowired
    AuditLogDtoService auditLogDtoService;
    @Autowired
    RepositoryService repositoryService;

    @Override
    public void notify(DelegateExecution delegateExecution) throws Exception {
        String operationGUID = (String) delegateExecution.getVariable(CommonProcessConstants.OPERATION_GUID);
        String processID = delegateExecution.getProcessInstanceId();
        String stepID = TextUtils.camelCase(delegateExecution.getCurrentActivityName());
        String processDefId = delegateExecution.getProcessDefinitionId();
        ProcessDefinition procDef = repositoryService.getProcessDefinition(processDefId);
        String msgKey = "$.operation.step." + procDef.getKey() + "." + stepID;
        auditLogDtoService.logOperationStepStart(msgKey, processID, operationGUID, stepID);
    }
}
