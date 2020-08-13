package pl.fhframework.core.uc.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.messages.IMessages;
import pl.fhframework.core.uc.FormsContainer;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.SessionManager;

/**
 * Created by pawel.ruta on 2018-11-26.
 */
@Component
public class NoFormInfoMessage implements INoFormHandler {
    @Autowired
    private IMessages messages;

    @Autowired
    private MessageService messageService;

    @Override
    public void handleNoFormCase(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext eventUseCaseContext, FormsContainer formsContainer) {
        messages.showError(SessionManager.getUserSession(), messageTitle(), buildMessage(), null);
    }

    @Override
    public String buildMessage() {
        return messageService.getAllBundles().getMessage(INoFormHandler.NO_FORM_MESSAGE);
    }
}
