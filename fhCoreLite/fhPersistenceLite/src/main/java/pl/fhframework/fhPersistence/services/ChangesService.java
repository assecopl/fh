package pl.fhframework.fhPersistence.services;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.rules.Comment;
import pl.fhframework.core.services.FhService;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.fhPersistence.conversation.ConversationManager;
import pl.fhframework.fhPersistence.conversation.ConversationManagerUtils;

/**
 * Build-in service gives access to user information
 */
@FhService(groupName = "changes", description = "Service for managing objects states", categories = {"data", "changes"})
public class ChangesService {
    @Autowired
    ConversationManagerUtils conversationManagerUtils;

    @Comment("Force transaction commit for changed values")
    public void save() {
        getConversationManager().approve(getConversationManager().getCurrentContext().getContextOwner());
    }

    @Comment("Force discard of changed values and restore original values")
    public void discard() {
        getConversationManager().restoreSnapshot(getConversationManager().getCurrentSnapshot().getOwner());
    }

    @Comment("Detach all objects from persistent context")
    public void clearContext() {
        getConversationManager().clear(getConversationManager().getCurrentContext().getContextOwner());
    }

    @Comment("Return if any objects are changed within current business transaction")
    public boolean areAny() {
        return getConversationManager().getCurrentSnapshot().isModified();
    }

    @Comment("Return if object is changed within current business transaction")
    public boolean areIn(@Parameter(name = "object", comment = "object to check for changes") ISnapshotEnabled object) {
        return getConversationManager().getCurrentSnapshot().isObjectModified(object);
    }

    private ConversationManager getConversationManager() {
        return conversationManagerUtils.getConversationManager();
    }
}
