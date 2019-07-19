package pl.fhframework.core.uc.handlers;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.messages.IMessages;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.SessionManager;

/**
 * Created by pawel.ruta on 2018-11-26.
 */
public class RunUseCaseHandler {
    @Autowired
    private IMessages messages;

    @Value("${initial.use.case:}")
    @Getter
    private String autostartedUseCase;

    protected void showMessageAndRunUseCase(UseCaseContainer useCaseContainer, String title, String message, String redirectUseCase) {
        messages.showError(SessionManager.getUserSession(), title, message, () -> {
            if (!StringUtils.isNullOrEmpty(redirectUseCase)) {
                useCaseContainer.runInitialUseCase(redirectUseCase);
            }
            else if (!StringUtils.isNullOrEmpty(autostartedUseCase)) {
                useCaseContainer.runInitialUseCase(autostartedUseCase);
            }
            else {
                useCaseContainer.clearUseCaseStack();
            }
        });
    }
}
