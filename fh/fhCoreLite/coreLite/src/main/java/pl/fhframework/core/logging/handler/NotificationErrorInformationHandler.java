package pl.fhframework.core.logging.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.fhframework.WebSocketContext;
import pl.fhframework.core.logging.ErrorInformation;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.FormsHandler;
import pl.fhframework.UserSession;
import pl.fhframework.SessionManager;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.model.dto.IErrorCarrierMessage;
import pl.fhframework.model.dto.OutMessageEventHandlingResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Adam Zareba on 31.01.2017.
 */
@Service
public class NotificationErrorInformationHandler implements NonResponsiveInformationHandler {

    @Autowired
    private EventRegistry eventRegistry;

    @Override
    public boolean handle(UserSession session, IErrorCarrierMessage message, List<ErrorInformation> errors, FormsHandler formsHandler, String requestId) {
        if (errors.size() > 0) {
            eventRegistry.fireNotificationEvent(NotificationEvent.Level.ERROR,
                    buildMessage(errors));
        }

        return true;
    }

    @Override
    public boolean handleFailure(boolean isSendingMessageForbidden, FormsHandler formsHandler, String requestId, List<ErrorInformation> errors) {
        if (isSendingMessageForbidden) {
            return false;
        }
        try {
            // this will try to send errors to client
            formsHandler.finishEventHandling(requestId, WebSocketContext.fromThreadLocals());
            return true;
        } catch (Exception finishingException) {
            // don't add this to errors being sent to client - just log
            FhLogger.errorSuppressed(finishingException);

            try {
                // just send already collected changes and events (notifications also)
                OutMessageEventHandlingResult eventHandlingResult = new OutMessageEventHandlingResult();
                eventHandlingResult.setEvents(SessionManager.getUserSession().getUseCaseRequestContext().getEvents());
                formsHandler.sendResponse(requestId, eventHandlingResult);
                SessionManager.getUserSession().getUseCaseRequestContext().getEvents().clear();
                return true;
            } catch (Exception sendingException) {
                // don't add this to errors being sent to client - just log
                FhLogger.errorSuppressed(finishingException);
            }

            return false;
        }
    }

    @Override
    public void onEventHandleError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, Throwable error, FormsHandler formsHandler, String requestId) {

    }

    @Override
    public String buildMessage(List<ErrorInformation> errors) {
        return errors.stream().map(NonResponsiveInformationHandler::buildSingleErrorMessage).collect(Collectors.joining(System.getProperty("line.separator")));
    }
}
