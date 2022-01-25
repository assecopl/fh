package pl.fhframework.dp.commons.fh.document.handling;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;
import pl.fhframework.dp.transport.dto.commons.OperationStepDto;
import pl.fhframework.model.forms.Form;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
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
        String operationGUID;
        private IDocumentHandler documentHandler;
        private OperationStateResponseDto operationStateResponse = new OperationStateResponseDto();
        private OperationStepDto selectedStep;
        private boolean internal = false;

        public Model() {
        }
    }
}
