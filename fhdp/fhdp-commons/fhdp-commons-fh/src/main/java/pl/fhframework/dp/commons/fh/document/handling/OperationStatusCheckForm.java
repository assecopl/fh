package pl.fhframework.dp.commons.fh.document.handling;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;
import pl.fhframework.dp.transport.dto.commons.OperationStepDto;
import pl.fhframework.dp.transport.service.IOperationDtoService;
import pl.fhframework.model.forms.Form;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 25.10.2019
 */
public class OperationStatusCheckForm extends Form<OperationStatusCheckForm.Model> {

    public OperationStatusCheckForm() {
    }

    public String formatDuration(float duration) {
        return duration != 0 ? String.format("%.2f", duration) : "";
    }

    @Getter
    @Setter
    public static class Model {
        private String operationGUID;
        private Long docId;
        private String processId;

        private IDocumentHandler documentHandler;
        private IOperationDtoService operationDtoService;
        private OperationStateResponseDto operationStateResponse = new OperationStateResponseDto();
        private OperationStepDto selectedStep;
        private boolean internal = false;
        private int timerTimeout = 1000;

        public Model() {
        }
    }
}
