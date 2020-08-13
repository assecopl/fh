package pl.fhframework.model.forms.logging.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.fhframework.FormsHandler;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.WebSocketContext;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.logging.ErrorInformation;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.logging.handler.NonResponsiveInformationHandler;
import pl.fhframework.core.uc.handlers.IOnActionErrorHandler;
import pl.fhframework.core.uc.handlers.RunUseCaseHandler;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.dto.IErrorCarrierMessage;
import pl.fhframework.model.dto.OutMessageEventHandlingResult;
import pl.fhframework.model.forms.messages.CoreKeysMessages;

import java.util.List;

/**
 * Created by Adam Zareba on 31.01.2017.
 */
@Service
public class MessageErrorInformationHandler extends RunUseCaseHandler implements NonResponsiveInformationHandler {

    private static final String NEW_LINE = System.getProperty("line.separator");

    @Value("${usecase.action.error.redirect:}")
    private String actionRedirectUseCase;

    @Value("${system.error.redirect:}")
    private String redirectUseCase;

    @Autowired
    private MessageService messageService;

    @Override
    public boolean handle(UserSession session, IErrorCarrierMessage message, List<ErrorInformation> errors, FormsHandler formsHandler, String requestId) {
        message.setErrors(errors);

        return true;
    }

    @Override
    public boolean handleFailure(boolean isSendingMessageForbidden, FormsHandler formsHandler, String requestId, List<ErrorInformation> errors) {
        if (isSendingMessageForbidden) {
            return false;
        }
        UserSession userSession = SessionManager.getUserSession();
        if (userSession.getUseCaseContainer().getCurrentUseCaseContext() != null) {
            try {
                showMessageAndRunUseCase(userSession.getUseCaseContainer(), messageTitle(), buildMessage(errors), StringUtils.nvl(redirectUseCase, actionRedirectUseCase));
                errors.clear();
                // this will try to send errors to client
                formsHandler.finishEventHandling(requestId, WebSocketContext.fromThreadLocals());
                return true;
            } catch (Exception finishingException) {
                // don't add this to errors being sent to client - just log
                FhLogger.errorSuppressed(finishingException);

                return false;
            }
        }
        else {
            try {
                OutMessageEventHandlingResult response = new OutMessageEventHandlingResult();
                response.setErrors(errors);
                // this will try to send errors to client
                formsHandler.sendResponse(requestId, response);
                return true;
            } catch (Exception sendingException) {
                // don't add this to errors being sent to client - just log
                FhLogger.errorSuppressed(sendingException);
                return false;
            }
        }
    }

    @Override
    public String buildMessage(List<ErrorInformation> errors) {
        StringBuilder message = new StringBuilder(buildGeneralMessage(errors));
        message.append(NEW_LINE);
        message.append(NEW_LINE);
        for (ErrorInformation error : errors) {
            message.append(NonResponsiveInformationHandler.buildSingleErrorMessage(error));
            message.append(NEW_LINE);
        }
        message.append(NEW_LINE);
        message.append(buildFooterMessage());
        return message.toString();
    }

    protected String buildGeneralMessage(List<ErrorInformation> errors) {
        return messageService.getAllBundles().getMessage(IOnActionErrorHandler.ACTION_ERROR_MESSAGE);
    }

    protected String buildFooterMessage() {
        UserSession userSession = SessionManager.getUserSession();

        String login = userSession.getSystemUser().getLogin();
        String sessionUniqueId = userSession.getConversationUniqueId();

        String defaultMsg = String.format("Current user is %s with session ID %s.", login, sessionUniqueId);
        Object[] params = {login, sessionUniqueId};

        return getAllMessgages().getMessage(CoreKeysMessages.CURRENT_USER_WITH_SESSTION, params, defaultMsg);
    }

    private MessageService.MessageBundle getAllMessgages() {
        return messageService.getAllBundles();
    }
}
