package pl.fhframework.fhPersistence.conversation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.FhConversationException;
import pl.fhframework.core.session.scope.SessionScope;
import pl.fhframework.fhPersistence.snapshots.ModelObjectManager;
import pl.fhframework.fhPersistence.snapshots.model.Snapshot;
import pl.fhframework.fhPersistence.transaction.ConversationStatelessContext;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  Class responsible for managing conversation contexts
 *  Conversation context are stored as a stack in which last element represent current(ongoing) conversation context
 *  Previous contexts are suspended and can be resumed when current conversation is completed
 */
@Component
@Scope(scopeName = SessionScope.SESSION_SCOPE, proxyMode = ScopedProxyMode.INTERFACES)
public class ConversationManagerImpl implements BusinessContext, ConversationManager {
    @Autowired
    private Environment env;

    @Autowired
    protected ApplicationContext applicationContext;

    protected Deque<ConversationContext> conversationContexts = new LinkedList<ConversationContext>();

    @Autowired
    protected ModelObjectManager modelObjectManager;

    private Set<Integer> initializingSnapshotObjects = Collections.newSetFromMap(new ConcurrentHashMap<>());

    //-----------------BUSINESS INTERFACES METHODS------------------------------------------------------------------

    @Override
    public void startOrContinue(Object owner) {
        startNewContextOrAttachToExistingOne(owner);
    }

    @Override
    public void start(Object owner, Object[] inputs) {
        ConversationContext prevContext = null;
        if (!conversationContexts.isEmpty()) {
            prevContext = conversationContexts.getLast();
        }

        startOrContinue(owner);

        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = modelObjectManager.exchangeManagedObjects(inputs[i], prevContext, getCurrentContext(), true);
        }
    }

    @Override
    public void approve(Object owner) {
        approveCurrentContext(owner);
    }

    @Override
    public void complete(Object owner) {
        approveAndTerminateCurrentContext(owner);
    }

    private void managePersistenceSession(Object owner) {
        exchangeFinishParams(owner, conversationContexts.getLast().getOutputParams());
        clearExchangedObjects(owner);
    }

    private void clearExchangedObjects(Object owner) {
        if (isOwner(owner) && conversationContexts.size() > 1) {
            ConversationContext current = conversationContexts.removeLast();
            ConversationContext prev = conversationContexts.getLast();
            conversationContexts.addLast(current);

            modelObjectManager.clearExchangedObjects(prev, current);
        }
    }

    public void withdraw(Object owner) {
        withdrawCurrentContext(owner);
    }

    public void clear(Object owner) {
        checkContextExistance("Attempt to clear not existing context");
        getCurrentContext().clear();
    }

    //-----------------SNAPSHOTS METHODS------------------------------------------------------------------

    @Override
    public void createSnapshot(final Object owner) {
        modelObjectManager.createSnapshot(owner, getCurrentContext());
    }

    @Override
    public void dropSnapshot(final Object owner) {
        modelObjectManager.dropSnapshot(owner, getCurrentContext());
    }

    @Override
    public void restoreSnapshot(final Object owner) {
        modelObjectManager.restoreSnapshot(owner, getCurrentContext());
        getCurrentSnapshot().setSuspended(false);
    }

    @Override
    public void restoreAndDropSnapshot(final Object owner) {
        modelObjectManager.restoreSnapshot(owner, getCurrentContext());
        modelObjectManager.dropSnapshot(owner, getCurrentContext());
    }

    @Override
    public boolean turnOff(Object source) {
        if (!conversationContexts.isEmpty()) {
            if (conversationContexts.getLast().isOwner(source)) {
                conversationContexts.getLast().setState(ConversationContext.STATE.SUSPENDED);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean turnOn(Object source) {
        if (!conversationContexts.isEmpty()) {
            if (conversationContexts.getLast().isOwner(source)) {
                conversationContexts.getLast().setState(ConversationContext.STATE.CURRENT);

                return true;
            }
        }

        return false;
    }

    //-----------------TOOLS INTERFACE METHODS-----------------------------------------------------------------

    public boolean contextExits() {
        return (!conversationContexts.isEmpty()) && conversationContexts.getLast().getState() == ConversationContext.STATE.CURRENT ;
    }

    public ConversationContext getCurrentContext() {
        checkContextExistance("Attempt to get not existing context");
        return conversationContexts.getLast();
    }

    public boolean snapshotExists() {
        return contextExits() && getCurrentContext().snapshotExists();
    }

    public Snapshot getCurrentSnapshot() {
        checkSnapshotExistance("Attempt to get not existing snapshot");
        return conversationContexts.getLast().getCurrentSnapshot();
    }

    public void suspendCurrentContext(Object owner) {
        checkContextExistance("Attempt to suspend not existing context");
        if (isOwner(owner)) {
            conversationContexts.getLast().suspend();
        }

    }

    public void markInitializing(ISnapshotEnabled object) {
        initializingSnapshotObjects.add(System.identityHashCode(object));
    }

    public void unmarkInitializing(ISnapshotEnabled object) {
        initializingSnapshotObjects.remove(System.identityHashCode(object));
    }

    public boolean isInitialized(ISnapshotEnabled object) {
        return !initializingSnapshotObjects.contains(System.identityHashCode(object));
    }

    @Override
    public boolean isContextValid() {
        if (contextExits()) {
            return getCurrentContext().isValid();
        }
        return false;
    }

    private void exchangeFinishParams(Object owner, Object[] outputs) {
        if (isOwner(owner) && conversationContexts.size() > 1) {
            ConversationContext current = conversationContexts.removeLast();
            ConversationContext prev = conversationContexts.getLast();
            conversationContexts.addLast(current);

            for (int i = 0; i < outputs.length; i++) {
                outputs[i] = modelObjectManager.exchangeManagedObjects(outputs[i], current, prev, false);
            }
        }
    }

    //-------------------------------------------------------------------------------------

    private void withdrawCurrentContext(Object owner) {
        checkContextExistance("Attempt to withdraw not existing context");
        restoreAndDropSnapshot(owner);
        if (isOwner(owner)) {
            getCurrentContext().terminate();
            closeCurrentContext();
        }
    }


    private void startNewContextOrAttachToExistingOne(Object owner) {
        if (conversationContexts.isEmpty() || conversationContexts.getLast().getState() == ConversationContext.STATE.SUSPENDED) {
            createContext(owner);
        }
        createSnapshot(owner);
    }

    private void startNewContext(Object owner) {
        if (!conversationContexts.isEmpty()) {
            throw new FhConversationException("Attempt to restart yet created context");
        }
        createContext(owner);
    }


    private void suspendCurrentContextAndStartNewOne(Object owner) {
        checkContextExistance("Attempt to suspend not existing context");
        if (isOwner(owner)) {
            conversationContexts.getLast().suspend();
            createContext(owner);
        }
    }


    private void approveCurrentContext(Object owner) {
        checkContextExistance("Attempt to approve not existing context");
        if (isOwner(owner)) {
            modelObjectManager.synchronizeObjectState();
            getCurrentContext().approve();
            managePersistenceSession(owner);
            getCurrentContext().approve();
        }
        Object snapshotOwner = getCurrentSnapshot().getOwner();
        dropSnapshot(snapshotOwner);
        createSnapshot(snapshotOwner);
    }

    private void approveAndTerminateCurrentContext(Object owner) {
        checkContextExistance("Attempt to approve and terminate not existing context");
        if (isOwner(owner)) {
            modelObjectManager.synchronizeObjectState();
            getCurrentContext().approve();
            managePersistenceSession(owner);
            getCurrentContext().approveAndTerminate();
            dropSnapshot(owner);
            closeCurrentContext();
        }
        else {
            dropSnapshot(owner);
        }
    }

    protected void createContext(Object owner) {
        ConversationContext context = (env.acceptsProfiles(Profiles.of("withoutDataSource"))) ?
                applicationContext.getBean(ConversationStatelessContext.class) :
                applicationContext.getBean(ConversationStatefullContext.class);
        context.setContextOwner(owner);
        conversationContexts.addLast(context);
    }

    private void checkContextExistance(String aMessage) {
        if (!contextExits()) {
            throw new FhConversationException(aMessage);
        }
    }

    private boolean isOwner(Object owner) {
        return getCurrentContext().isOwner(owner);
    }

    private void closeCurrentContext() {
        conversationContexts.removeLast();
        if (!conversationContexts.isEmpty()) {
            conversationContexts.getLast().setState(ConversationContext.STATE.CURRENT);
        }

    }

    private void checkSnapshotExistance(String aMessage) {
        if (!snapshotExists()) {
            throw new FhConversationException(aMessage);
        }
    }
}
