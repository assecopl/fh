package pl.fhframework.core.logging.handler;

import pl.fhframework.core.logging.ErrorInformation;
import pl.fhframework.FormsHandler;
import pl.fhframework.UserSession;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.core.uc.handlers.IOnEventHandleError;
import pl.fhframework.model.dto.IErrorCarrierMessage;

import java.util.List;

/**
 * Interface of FH handler for errors informations awaiting to be sent to client.
 */
public interface IErrorInformationHandler extends IOnEventHandleError {

    /**
     * Handles errors informations. Typically shows them to client.
     * No need for logging them on server side as they are already logged.
     * Returning true will cause discarding this information from user session.
     *
     * @param session user session
     * @param message message to be sent
     * @param errors errors
     * @param formsHandler
     * @param requestId
     * @return if handling was successful
     */
    public boolean handle(UserSession session, IErrorCarrierMessage message, List<ErrorInformation> errors, FormsHandler formsHandler, String requestId);

    /**
     * Handles errors informations after request service failure. Typically creates new message and sends errors to client.
     * No need for logging them on server side as they are already logged.
     * Returning true will cause discarding this information from user session.
     *
     * @param isSendingMessageForbidden is sending a new message forbidden
     * @param requestId request id
     * @param formsHandler forms handler
     * @param errors errors
     * @return if handling was successful
     */
    public boolean handleFailure(boolean isSendingMessageForbidden, FormsHandler formsHandler, String requestId, List<ErrorInformation> errors);

    /**
     * Method for handling exceptions occurred within usecase context, but outside of usecase action
     * method - before action or after action (eg during form update)
     *  @param useCaseContainer use case container
     * @param useCaseContext use case context
     * @param error exception
     * @param formsHandler
     * @param requestId
     */
    default void onEventHandleError(UseCaseContainer useCaseContainer, UseCaseContainer.UseCaseContext useCaseContext, Throwable error, FormsHandler formsHandler, String requestId) {

    }

    default String buildMessage(List<ErrorInformation> errors) {
        return null;
    }

    default String messageTitle() {
        return null;
    }
}
