package pl.fhframework.dp.commons.fh.document.handling;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.transport.dto.commons.OperationStateResponseDto;
import pl.fhframework.dp.transport.dto.commons.OperationStepDto;
import pl.fhframework.WebSocketContext;
import pl.fhframework.WebSocketFormsHandler;
import pl.fhframework.WebSocketSessionManager;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.UseCase;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 25.10.2019
 */
@UseCase
@Getter @Setter
public class OperationStatusCheckUC implements IUseCaseOneInput<OperationStatusCheckForm.Model, IUseCaseSaveCancelCallback<OperationStatusCheckForm.Model>> {

    private OperationStatusCheckForm.Model model;

    @Override
    public void start(OperationStatusCheckForm.Model one) {
        this.model = one;
        showForm(OperationStatusCheckForm.class, model);
        checkTask();
    }

    @Action(validate = true)
    public void save() {
        this.exit().save(model);
    }

    @Action(validate = false)
    public void cancel() {
        model.setTimerTimeout(0);
        getActiveForm().refreshView();
        exit().cancel();
    }

    @Action(validate = false)
    private void checkTask() {
        FhLogger.debug("Calling service for operation ID {}...", model.getOperationGUID());
        OperationStateResponseDto state = model.getDocumentHandler().checkOperationState(model.getOperationGUID());
        List<OperationStepDto> steps = state.getSteps();
        Collections.sort(steps);
        model.getOperationStateResponse().setSteps(steps);
        model.getOperationStateResponse().setFinished(state.isFinished());
        if (state.isFinished()) {
            model.setTimerTimeout(0);
            getActiveForm().refreshView();
            save();
        }
    }

}
