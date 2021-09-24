package pl.fhframework.fhPersistence.conversation;

/**
 * Conversation access method on the top (business level)
 */
public interface BusinessContext {

    /**
     * Start business context
     * @param owner owner of transaction
     */
    public void startOrContinue(Object owner);

    /**
     * Start business context with initial objects. Attaches inputs to EM and places then back to passed array.
     * @param owner owner of transaction
     * @param inputs initial objects
     */
    public void start(Object owner, Object[] inputs);

    /**
     * Approve (safe) business context without closing it
     * @param owner  owner of transaction
     */
    public void approve(Object owner);

    /**
     * Complete business context: approve and close business context
     * @param owner  owner of transaction
     */
    public void complete(Object owner);

    /**
     * Withraw changes in business context remove current conversation context ( and pop it from stack)
     * @param owner  owner of transaction
     */
    public void withdraw(Object owner);

    /**
     * Remove current conversation context ( and pop it from stack) and creates new context (there is no changes withdrawal
     * @param owner  owner of transaction
     */
    public void clear(Object owner);

    /**
     * Start new snapshot, aka nested transaction
     * @param owner identifier
     */
    void createSnapshot(Object owner);

    /**
     * Drop snapshot, changes made are accepted
     * @param owner identifier
     */
    void dropSnapshot(Object owner);

    /**
     * Withdraw changes made during nested transaction and drop snapshot
     * @param owner identifier of snapshot
     */
    void restoreAndDropSnapshot(Object owner);

    /**
     * Withdraw changes made during nested transaction
     * @param owner identifier of snapshot
     */
    void restoreSnapshot(Object owner);

    boolean isContextValid();
}
