package pl.fhframework.dp.transport.dto.operations;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;
import pl.fhframework.dp.transport.enums.NullableBooleanEnum;
import pl.fhframework.dp.transport.enums.PerformerEnum;

import java.time.LocalDateTime;

@Getter @Setter
public class OperationDtoQuery extends BaseDtoQuery {
    private String operationCode;
    private LocalDateTime formalDateFrom;
    private LocalDateTime formalDateTo;

    private NullableBooleanEnum hasAnnotation = NullableBooleanEnum.NONE;
    private NullableBooleanEnum hasChanges = NullableBooleanEnum.NONE;

    private PerformerEnum performerType = PerformerEnum.ANY;
    private String performer;
}
