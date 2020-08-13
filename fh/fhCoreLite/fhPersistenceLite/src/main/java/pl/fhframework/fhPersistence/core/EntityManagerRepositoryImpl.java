package pl.fhframework.fhPersistence.core;

import org.eclipse.core.runtime.Assert;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.fhframework.fhPersistence.conversation.ConversationManager;
import pl.fhframework.fhPersistence.conversation.ConversationManagerUtils;
import pl.fhframework.SessionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

@Component(EntityManagerRepository.beanName)
@Profile("!withoutDataSource")
//@ConditionalOnBean(name = "FhPU")
public class EntityManagerRepositoryImpl implements EntityManagerRepository {
    private static final ThreadLocal<Integer> withoutConversation = ThreadLocal.withInitial(() -> 0);

    @PersistenceContext(unitName="FhPU", type = PersistenceContextType.TRANSACTION)
    private EntityManager entityManager;

    @Autowired
    ApplicationContext applicationContext;

    public EntityManager getEntityManager(){
        if (isConversation()) {
            ConversationManager conversationManager = getConversationManager();
            if (conversationManager != null && conversationManager.contextExits()) {
                return conversationManager.getCurrentContext().getEntityManager();
            }
        }
        return entityManager;
    }

    @Override
    public void turnOffConvesation(Object source) {
        withoutConversation.set(withoutConversation.get() + 1);
    }

    @Override
    public void turnOffSessionConvesation(Object source) {
        ConversationManager conversationManager = getConversationManager();
        if (conversationManager != null) {
            conversationManager.turnOff(source);
        }
    }

    @Override
    public void turnOnConvesation(Object source) {
        withoutConversation.set(withoutConversation.get() - 1);
        Assert.isTrue(withoutConversation.get() >= 0, "Incorrect use of conversation on/off switch mechanism");
    }

    @Override
    public void turnOnSessionConvesation(Object source) {
        ConversationManager conversationManager = getConversationManager();
        if (conversationManager != null) {
            conversationManager.turnOn(source);
        }
    }

    private ConversationManager getConversationManager() {
        // Service / dao can be called without web context (eg. Timer), then would be exception on @Autowired,
        // because ConversationManager bean have Session scope.
        if (isValidSession()) {
            return applicationContext.getBean(ConversationManagerUtils.class).getConversationManager();
        }

        return null;
    }

    private boolean isValidSession(){
        return SessionManager.getSession() != null;
    }

    @Override
    public boolean isConversation() {
        return isConversationOn();
    }

    public static boolean isConversationOn() {
        return withoutConversation.get() == 0;
    }
}
