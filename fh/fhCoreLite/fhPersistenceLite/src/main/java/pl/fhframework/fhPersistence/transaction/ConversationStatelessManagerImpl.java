package pl.fhframework.fhPersistence.transaction;


import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.fhPersistence.conversation.ConversationManagerImpl;
import pl.fhframework.fhPersistence.core.EntityManagerRepository;
import pl.fhframework.helper.AutowireHelper;

/**
 *  Class responsible for managing stateless conversation contexts
 *  Conversation context are stored as a stack in which last element represent current(ongoing) conversation context
 *  Previous contexts are suspended and can be resumed when current conversation is completed
 */
public class ConversationStatelessManagerImpl extends ConversationManagerImpl {
    @Autowired
    private EntityManagerRepository emRepository;

    public ConversationStatelessManagerImpl() {
        AutowireHelper.autowire(this, modelObjectManager, applicationContext, emRepository);
    }

    @Override
    protected void createContext(Object owner) {
        ConversationStatelessContext context = applicationContext.getBean(ConversationStatelessContext.class);
        context.setEntityManager(emRepository.getEntityManager().unwrap(Session.class));
        context.setContextOwner(owner);
        conversationContexts.addLast(context);
    }
}
