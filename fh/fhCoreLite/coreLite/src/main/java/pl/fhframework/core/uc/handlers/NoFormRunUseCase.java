package pl.fhframework.core.uc.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.messages.IMessages;
import pl.fhframework.core.uc.FormsContainer;
import pl.fhframework.core.uc.UseCaseContainer;

/**
 * Created by pawel.ruta on 2018-11-26.
 */
@Component
public class NoFormRunUseCase extends RunUseCaseHandler implements INoFormHandler {
    @Autowired
    private IMessages messages;

    @Autowired
    private MessageService messageService;

    @Value("${initial.use.case:}")
    private String autostartedUseCase;

    @Value("${usecase.empty.form.stack.redirect:}")
    private String redirectUseCase;

    @Override
    public void handleNoFormCase(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext eventUseCaseContext, FormsContainer formsContainer) {
        showMessageAndRunUseCase(useCaseContainer, messageTitle(), buildMessage(), redirectUseCase);
    }

    @Override
    public String buildMessage() {
        return messageService.getAllBundles().getMessage(INoFormHandler.NO_FORM_MESSAGE);
    }
}
