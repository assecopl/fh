package pl.fhframework.fhPersistence.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import pl.fhframework.aop.services.IFhTransactionListener;
import pl.fhframework.core.FhException;
import pl.fhframework.fhPersistence.conversation.ConversationManager;
import pl.fhframework.fhPersistence.conversation.ConversationManagerUtils;

/**
 * Created by pawel.ruta on 2018-03-19.
 */
@Service
public class FhTransactionListenerImpl implements IFhTransactionListener {
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onStart(Integer propagation, Object owner, Object[] params) {
        if (propagation != null) {
            Propagation propagationEnum = Propagation.values()[propagation];
            // todo: support for all types
            if (propagationEnum != Propagation.REQUIRES_NEW && propagationEnum != Propagation.REQUIRED) {
                throw new FhException(String.format("Not supported '%s' transaction propagation for Service", propagationEnum.name()));
            }
            if (getConversationManager().contextExits() && propagationEnum == Propagation.REQUIRES_NEW) {
                getConversationManager().suspendCurrentContext(getConversationManager().getCurrentContext().getContextOwner());
                getConversationManager().start(owner, params);
            }
            else {
                getConversationManager().startOrContinue(owner);
            }
        }
    }

    @Override
    public void onEnd(Integer propagation, Object owner, Object[] outputParams) {
        if (propagation != null) {
            registerOutputParam(owner, outputParams);

            getConversationManager().complete(owner);
        }
    }

    @Override
    public void onError(Integer propagation, Object owner) {
        if (propagation != null) {
            // rollback
            while (getConversationManager().contextExits()) {
                while (getConversationManager().getCurrentContext().getSnapshotsStack().size() > 1) {
                    boolean contextOwner = getConversationManager().getCurrentSnapshot().isOwner(owner);
                    getConversationManager().withdraw(getConversationManager().getCurrentSnapshot().getOwner());
                    if (contextOwner) {
                        return;
                    }
                }
                boolean contextOwner = getConversationManager().getCurrentContext().isOwner(owner);
                getConversationManager().withdraw(getConversationManager().getCurrentContext().getContextOwner());
                if (contextOwner) {
                    return;
                }
            }
        }
    }

    @Override
    public void invalidate() {
        if (getConversationManager().contextExits()) {
            getConversationManager().getCurrentContext().invalidate();
        }
    }

    private void registerOutputParam(Object owner, Object[]  params) {
        ConversationManager conversationManager = getConversationManager();
        if (conversationManager.contextExits() && conversationManager.getCurrentContext().isOwner(owner)) {
            conversationManager.getCurrentContext().setOutputParams(params);
        }
    }

    private ConversationManager getConversationManager() {
        return applicationContext.getBean(ConversationManagerUtils.class).getConversationManager();
    }
}
