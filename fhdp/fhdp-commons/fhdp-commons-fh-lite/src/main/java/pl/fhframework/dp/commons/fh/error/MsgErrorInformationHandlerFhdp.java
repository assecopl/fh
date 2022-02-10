package pl.fhframework.dp.commons.fh.error;

import lombok.Getter;
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
import pl.fhframework.core.uc.handlers.RunUseCaseHandler;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.model.dto.IErrorCarrierMessage;
import pl.fhframework.model.dto.OutMessageEventHandlingResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static pl.fhframework.core.logging.ErrorInformation.ErrorType.UNSPECIFIED_ERROR;

/**
 * Created by Adam Zareba on 31.01.2017.
 */
@Service
public class MsgErrorInformationHandlerFhdp extends RunUseCaseHandler implements NonResponsiveInformationHandler {

    @Getter
    @Value("${error.emailAddresses:}")
    private String emailAddresses;

    private static final String NEW_LINE = System.getProperty("line.separator");
    @Value("${usecase.action.error.redirect:}")
    private String actionRedirectUseCase;
    @Value("${system.error.redirect:}")
    private String redirectUseCase;
    @Autowired
    private MessageService messageService;
    @Autowired
    private EventRegistry eventRegistry;

    public boolean handle(UserSession session, IErrorCarrierMessage message, List<ErrorInformation> errors, FormsHandler formsHandler, String requestId) {
        List<ErrorInformation> myErrors = new ArrayList<>();
        ErrorInformation me = new ErrorInformation();
        me.setErrorType(UNSPECIFIED_ERROR);
        me.setMessage("Ups.. Huston, we got problem...");
        me.setTimestamp(LocalDateTime.now());
        myErrors.add(me);
        message.setErrors(myErrors);
//        eventRegistry.fireNotificationEvent(NotificationEvent.Level.ERROR, "Ups...");
        return true;
    }

    public boolean handleFailure(boolean isSendingMessageForbidden, FormsHandler formsHandler, String requestId, List<ErrorInformation> errors) {
        if (isSendingMessageForbidden) {
            return false;
        } else {
            UserSession userSession = SessionManager.getUserSession();
            if (userSession.getUseCaseContainer().getCurrentUseCaseContext() != null) {
                try {
                    this.showMessageAndRunUseCase(userSession.getUseCaseContainer(), this.messageTitle(), this.buildMessage(errors), StringUtils.nvl(new String[]{this.redirectUseCase, this.actionRedirectUseCase}));
                    errors.clear();
                    formsHandler.finishEventHandling(requestId, WebSocketContext.fromThreadLocals());
                    return true;
                } catch (Exception var7) {
                    FhLogger.errorSuppressed(var7);
                    return false;
                }
            } else {
                try {
                    OutMessageEventHandlingResult response = new OutMessageEventHandlingResult();
                    response.setErrors(errors);
                    formsHandler.sendResponse(requestId, response);
                    return true;
                } catch (Exception var8) {
                    FhLogger.errorSuppressed(var8);
                    return false;
                }
            }
        }
    }

    public String buildMessage(List<ErrorInformation> errors) {
        StringBuilder message = new StringBuilder(this.buildGeneralMessage(errors));
        message.append(NEW_LINE);
        message.append(NEW_LINE);
        Iterator var3 = errors.iterator();

        while(var3.hasNext()) {
            ErrorInformation error = (ErrorInformation)var3.next();
            message.append(NonResponsiveInformationHandler.buildSingleErrorMessage(error));
            message.append(NEW_LINE);
        }

        message.append(NEW_LINE);
        message.append(this.buildFooterMessage());
        return message.toString();
    }

    protected String buildGeneralMessage(List<ErrorInformation> errors) {
        return this.messageService.getAllBundles().getMessage("fh.core.action.error");
    }

    protected String buildFooterMessage() {
        UserSession userSession = SessionManager.getUserSession();
        String login = userSession.getSystemUser().getLogin();
        String sessionUniqueId = userSession.getConversationUniqueId();
        String defaultMsg = String.format("Current user is %s with session ID %s.", login, sessionUniqueId);
        Object[] params = new Object[]{login, sessionUniqueId};
        return this.getAllMessgages().getMessage("fh.core.toast.current_user_with_session", params, defaultMsg);
    }

    private MessageService.MessageBundle getAllMessgages() {
        return this.messageService.getAllBundles();
    }
}
