package pl.fhframework.fhPersistence.conversation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import pl.fhframework.aspects.conversation.IUseCaseConversation;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.session.scope.SessionScope;
import pl.fhframework.fhPersistence.anotation.Approve;
import pl.fhframework.fhPersistence.anotation.Cancel;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Paweł Ruta
 */
@Component
@Scope(scopeName = SessionScope.SESSION_SCOPE, proxyMode = ScopedProxyMode.INTERFACES)
public class UseCaseConversationImpl implements IUseCaseConversation {
    @Autowired
    ConversationManager conversationManager;
    Map<Object, ConversationParams> conversationParams = new HashMap<>();

    public void saveChnages(Object owner) {
        conversationManager.approve(owner);
    }

    public void cancelChanges(Object owner) {
        conversationManager.restoreSnapshot(owner);
    }

    @Override
    public void subUsecaseStarted(Object owner) {
        conversationManager.startOrContinue(owner);
    }

    @Override
    public void usecaseStarted(Object owner, Object[] inputData) {
        if (conversationManager.contextExits()) {
            ConversationContext cc = conversationManager.getCurrentContext();
            conversationManager.suspendCurrentContext(cc.getContextOwner());
        }

        conversationManager.start(owner, inputData);
    }

    @Override
    public void registerOutputParams(Object owner, Object[] args) {
        if (conversationManager.contextExits() && conversationManager.getCurrentContext().isOwner(owner)) {
            conversationManager.getCurrentContext().setOutputParams(args);
        }
    }

    @Override
    public void usecaseEnded(Object owner) {
        ConversationParams cp = getOrCreate(owner, true);
        removeConversationParam(owner);
        if (cp.isCancel()) {
            conversationManager.withdraw(owner);
        } else {
            conversationManager.complete(owner);
        }
    }

    private void removeConversationParam(Object owner) {
        FhLogger.debug("Removing conversation params for owner {}. {} left.", owner, conversationParams.size());
        ConversationParams ret = conversationParams.remove(owner);
        if(ret == null) {
            FhLogger.warn("***** *** usecaseEnded Object {} not removed from conversationParams!", owner);
        }
    }

    @Override
    public void usecaseTerminated(final Object owner) {
        conversationParams.remove(owner);
        conversationManager.withdraw(owner);
    }

    @Override
    public void processAnnotationsBeforeAction(final Method transition, final Object owner) {
        if (transition.getDeclaredAnnotation(Cancel.class) != null) {
            ConversationParams cp = getOrCreate(owner, true);
            cp.setCancel(true);
        }
        else if (transition.getDeclaredAnnotation(Approve.class) != null) {
            ConversationParams cp = getOrCreate(owner, true);
            cp.setApprove(true);
        }
    }

    @Override
    public void processAnnotationsAfterAction(final Method transition, final Object owner) {
        if (getOrCreate(owner, false).isCancel()) {
            conversationParams.remove(owner);
            cancelChanges(owner);
        }
        else if (getOrCreate(owner, false).isApprove()) {
            conversationParams.remove(owner);
            saveChnages(owner);
        }
    }

    private ConversationParams getOrCreate(final Object owner, boolean put) {
        ConversationParams cp = conversationParams.get(owner);
        if (cp == null) {
            cp = new ConversationParams();
            FhLogger.debug("Creating conversationParams for owner {}", owner);

            if (put == true) {
                conversationParams.put(owner, cp);
            }
        }

        return cp;
    }

    @Override
    public boolean isContextValid() {
        return conversationManager.isContextValid();
    }
}

@Getter
@Setter
class ConversationParams {
    private boolean cancel;
    private boolean approve;
}
