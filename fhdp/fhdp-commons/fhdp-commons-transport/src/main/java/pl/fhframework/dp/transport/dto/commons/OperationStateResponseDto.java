package pl.fhframework.dp.transport.dto.commons;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 28/05/2020
 */
@Getter @Setter
public class OperationStateResponseDto {
    private boolean finished;
    private String initialTaskId;
    private String initialTaskName;
    private String currentTaskID;
    private String currentTaskName;
    private List<OperationStepDto> steps = new ArrayList<>();
    private boolean incident;
    private String stackTrace;
}
