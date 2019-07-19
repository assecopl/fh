package pl.fhframework.fhPersistence.snapshots;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.fhframework.aspects.snapshots.SnapshotsManager;
import pl.fhframework.fhPersistence.conversation.ConversationManager;
import pl.fhframework.fhPersistence.conversation.ConversationManagerUtils;

/**
 */
@Component
public class SnapshotsManagerImpl implements SnapshotsManager {
    @Autowired
    private ConversationManagerUtils conversationManagerUtils;

    @Autowired
    private ModelObjectManager modelObjectManager;

    @Override
    public void createSnapshot(final Object owner) {
        modelObjectManager.createSnapshot(owner, getConversationManager().getCurrentContext());
    }

    @Override
    public void dropSnapshot(final Object owner) {
        modelObjectManager.dropSnapshot(owner, getConversationManager().getCurrentContext());
    }

    @Override
    public void restoreSnapshot(final Object owner) {
        modelObjectManager.restoreSnapshot(owner, getConversationManager().getCurrentContext());
    }

    private ConversationManager getConversationManager() {
        return conversationManagerUtils.getConversationManager();
    }
}
