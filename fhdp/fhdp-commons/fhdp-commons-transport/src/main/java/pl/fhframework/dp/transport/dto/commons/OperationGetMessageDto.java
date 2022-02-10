package pl.fhframework.dp.transport.dto.commons;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OperationGetMessageDto extends OperationDto {
    private String repositoryId;
}
