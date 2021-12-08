package pl.fhframework.dp.commons.fh.messages;

import pl.fhframework.dp.commons.fh.operations.msg.GetMessageOperationHandler;
import pl.fhframework.dp.transport.dto.commons.GetMessageOperationResultBaseDto;
import pl.fhframework.dp.transport.dto.commons.OperationGetMessageDto;
import pl.fhframework.core.services.FhService;

@FhService
public class GetMessageService extends GetMessageOperationHandler {

    public byte[] getMessageContent(String repositoryId) {
        OperationGetMessageDto dto = new OperationGetMessageDto();
        dto.setRepositoryId(repositoryId);
        GetMessageOperationResultBaseDto messageResultDto = performOperation(dto);
        return messageResultDto.getDocument().getContent();
    }
}
