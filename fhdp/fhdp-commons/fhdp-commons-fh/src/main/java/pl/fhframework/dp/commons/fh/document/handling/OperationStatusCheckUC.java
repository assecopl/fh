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
    private boolean run = true;
    Thread t;

    @Autowired
    private WebSocketFormsHandler formsHandler;

    @Override
    public void start(OperationStatusCheckForm.Model one) {
        this.model = one;
        showForm(OperationStatusCheckForm.class, model);
        WeakReference<WebSocketContext> webSocketContext = new WeakReference<>(WebSocketContext.fromThreadLocals());
        startCheckTask(this, formsHandler, webSocketContext);
    }

    @Action(validate = true)
    public void save() {
        this.exit().save(model);
    }

    @Action(validate = false)
    public void cancel() {
        run = false;
        t.interrupt();
        exit().cancel();
    }

    private void startCheckTask(final OperationStatusCheckUC uc, WebSocketFormsHandler formsHandler, WeakReference<WebSocketContext> webSocketContext) {
        t = new Thread(() -> checkTask(uc, formsHandler, webSocketContext));
        t.start();
    }

    private void checkTask(OperationStatusCheckUC uc, WebSocketFormsHandler formsHandler, WeakReference<WebSocketContext> webSocketContext) {
        try {
            WebSocketSessionManager.setWebSocketSession(webSocketContext.get().getWebSocketSession());
//            List<UseCaseContainer.UseCaseContext> runningUsecases = webSocketContext.get().getUserSession().getUseCaseContainer().getUseCases(OperationStatusCheckUC.class);
            while (run) {
                FhLogger.debug("Calling service for operation ID {}...", model.getOperationGUID());
                OperationStateResponseDto state = model.getDocumentHandler().checkOperationState(model.getOperationGUID());
                List<OperationStepDto> steps = state.getSteps();
                Collections.sort(steps);
                model.getOperationStateResponse().setSteps(steps);
                model.getOperationStateResponse().setFinished(state.isFinished());
                WebSocketContext webSocketCtx = webSocketContext.get();
                formsHandler.finishEventHandling("cos", webSocketCtx);
                FhLogger.debug("websocketContexa: {}", webSocketCtx);
                FhLogger.debug("Steps count: {}, finished: {}", state.getSteps().size(), state.isFinished());
                if (state.isFinished()) {
                    FhLogger.debug("Exit...");
                    save();
                    FhLogger.debug("[Exit] save()");
                    formsHandler.finishEventHandling("EXIT_UC", webSocketCtx);
                    FhLogger.debug("[Exit] EXIT_UC");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        } finally {
            FhLogger.debug("Cleaning...");
            // wyczyść sesję w wątku
            WebSocketSessionManager.setWebSocketSession(null);
        }

    }
}
