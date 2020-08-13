package pl.fhframework.core.logging;

import org.springframework.stereotype.Service;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.util.DebugUtils;
import pl.fhframework.Session;
import pl.fhframework.UserSession;
import pl.fhframework.model.dto.AbstractMessage;
import pl.fhframework.model.dto.InMessageEventData;

import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2019-02-11.
 */
@Service
public class SessionLogger implements ISessionLogger{
    public void logUserSessionCreation(Session newSession) {
        if (newSession instanceof UserSession) {
            FhLogger.info(this.getClass(), String.format("New user session %s.\n" +
                            "User login: %s\n" +
                            "Name: %s\n" +
                            "Surname: %s\n" +
                            "Security roles: %s\n" +
                            "Client info: %s\n" +
                            "Client address: %s\n" +
                            "Server address: %s\n",
                    newSession.getDescription().getConversationUniqueId(),
                    newSession.getSystemUser().getLogin(),
                    newSession.getSystemUser().getName(),
                    newSession.getSystemUser().getSurname(),
                    newSession.getSystemUser().getBusinessRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toSet()).toString(),
                    ((UserSession) newSession).getDescription().getClientInfo(),
                    ((UserSession) newSession).getDescription().getUserAddress(),
                    newSession.getDescription().getServerAddress()));
        } else {
            FhLogger.info(this.getClass(), String.format("New session %s.\n" +
                            "Login: %s\n" +
                            "Security roles: %s\n" +
                            "Server address: %s\n",
                    newSession.getDescription().getConversationUniqueId(),
                    newSession.getSystemUser().getLogin(),
                    newSession.getSystemUser().getBusinessRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toSet()).toString(),
                    newSession.getDescription().getServerAddress()));
        }
    }

    public void logSessionState(UserSession session) {
        FhLogger.info(this.getClass(), "Session State: "
                        + "\n   stackPU {}"
                        + "\n   form: {}",
                session.getUseCaseContainer().logState(),
                session.getUseCaseContainer().getFormsContainer().logState());
        if (!session.getUseCaseRequestContext().getFormsToHide().isEmpty())
            FhLogger.info(this.getClass(), "Forms to hide: {}", DebugUtils.collectionInfo(session.getUseCaseRequestContext().getFormsToHide()));
        if (!session.getUseCaseRequestContext().getFormsToDisplay().isEmpty())
            FhLogger.info(this.getClass(), "Forms to show: {}", DebugUtils.collectionInfo(session.getUseCaseRequestContext().getFormsToDisplay()));
    }

    public void logSessionEndState(UserSession session) {
        logSessionState(session);
    }

    @Override
    public void logEvent(InMessageEventData eventData) {

    }

    @Override
    public void logShowForm(Class<?> formClazz, Object model, String variantId) {
        
    }

    @Override
    public void logReqestResponse(String requestId, AbstractMessage inMessage, IUseCase topUseCase, Throwable exception, Long time) {
        if (FhLogger.isDebugEnabled(this.getClass())) {
            FhLogger.debug(this.getClass(), logger -> logger.log("Service time: {}", DebugUtils.timeAsString(time)));
        }
    }
}
