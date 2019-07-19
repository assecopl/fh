package pl.fhframework.fhPersistence.conversation;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.core.FhConversationException;
import pl.fhframework.core.FhStaleConversationException;
import pl.fhframework.fhPersistence.core.model.ModelStore;
import pl.fhframework.fhPersistence.snapshots.model.ExchangedEntities;
import pl.fhframework.fhPersistence.snapshots.model.Snapshot;
import pl.fhframework.ReflectionUtils;

import javax.persistence.EntityManager;
import java.util.Stack;


/**
 * Class responsible for keeping convesation State of the objects are maintain in EntityManager
 */
public abstract class ConversationContext {
    public enum STATE {CURRENT, SUSPENDED}

    @Setter
    @Getter
    private STATE state = STATE.CURRENT;

    @Setter
    @Getter
    private Object contextOwner;

    @Getter
    private Stack<Snapshot> snapshotsStack = new Stack<>();

    @Getter
    private ExchangedEntities exchangedEntities = new ExchangedEntities();

    @Getter
    @Setter
    private Object[] outputParams = new Object[]{};

    @Autowired
    @Setter
    private ModelStore dynamicModelStore;

    @Autowired
    ApplicationContext applicationContext;

    private boolean invalidToApprove = false;

    private static final ThreadLocal<ModelStore> overloadedDynamicModelStore = new ThreadLocal<>();

    public abstract EntityManager getEntityManager();

    public void suspend() {
        checkIfCurrent();
        setState(STATE.SUSPENDED);
    }

    public void terminate() {
        checkIfCurrent();
        getEntityManager().clear();
        ((Session) getEntityManager().getDelegate()).close();
    }

    @Transactional
    public void approve() {
        checkIfCurrent();
        if (invalidToApprove) {
            throw new FhStaleConversationException();
        }
        try {
            getEntityManager().flush();
        }
        catch (Exception e) {
            invalidate();

            throw e;
        }
    }

    @Transactional
    public void approveAndTerminate() {
        approve();
        ((Session) getEntityManager().getDelegate()).close();
    }

    public void clear() {
        checkIfCurrent();
        getEntityManager().clear();
        snapshotsStack.forEach(Snapshot::clear);
        dynamicModelStore = applicationContext.getBean(ModelStore.class);
        //dynamicModelStore.clearPersistentContext();
    }

    public boolean snapshotExists() {
        return !snapshotsStack.isEmpty();
    }

    public Snapshot getCurrentSnapshot() {
        return snapshotsStack.peek();
    }

    public boolean isOwner(Object contextOwner) {
        return ReflectionUtils.objectsEqual(this.contextOwner, contextOwner);
    }

    public void overLoadDynamicModelStore(ModelStore dynamicModelStore) {
        if (dynamicModelStore == null) {
            overloadedDynamicModelStore.remove();
        }
        else {
            overloadedDynamicModelStore.set(dynamicModelStore);
        }
    }

    public ModelStore getModelStore() {
        if (overloadedDynamicModelStore.get() != null) {
            return overloadedDynamicModelStore.get();
        }
        return dynamicModelStore;
    }

    public boolean isValid() {
        return !invalidToApprove;
    }

    private void checkIfCurrent() {
        if (state == STATE.SUSPENDED) {
            throw new FhConversationException("Attempt to execute operation on SUSPENDED context");
        }
    }

    public void invalidate() {
        invalidToApprove = true;
    }
}
