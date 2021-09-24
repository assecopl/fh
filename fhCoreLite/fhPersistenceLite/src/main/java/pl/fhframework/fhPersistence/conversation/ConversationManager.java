package pl.fhframework.fhPersistence.conversation;


import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.fhPersistence.snapshots.model.Snapshot;

/**
 * Conversation access method on the low technical(tools) level
 */
public interface ConversationManager extends BusinessContext {

    /**
     * Check if conversation context exits
     * @return
     */
    public boolean contextExits();

    /**
     * Return current (last on the stack) conversation context
     * @return
     */
    public ConversationContext getCurrentContext();

    /**
     * Suspend current conversation context. Suspending  allows to create new context on the stack (push)
     * @param owner
     */
    public void suspendCurrentContext(Object owner);

    /**
     * Check if snapshot exits
     * @return true if snapshot exits
     */
    boolean snapshotExists();

    /**
     * Return current (last on the stack) snapshot
     * @return current snapshot
     */
    Snapshot getCurrentSnapshot();

    /**
     * Turn off conversation context (suspends when exists).
     *
     * @return if conversation was suspended
     * @param source
     */
    boolean turnOff(Object source);

    /**
     * Turn on conversation context (activate when exists).
     *
     * @return if conversation was activated
     * @param source
     */
    boolean turnOn(Object source);

    void markInitializing(ISnapshotEnabled object);

    void unmarkInitializing(ISnapshotEnabled object);

    boolean isInitialized(ISnapshotEnabled object);
}
