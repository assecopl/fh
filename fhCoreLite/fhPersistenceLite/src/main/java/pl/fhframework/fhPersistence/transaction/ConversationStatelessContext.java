package pl.fhframework.fhPersistence.transaction;


import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.fhPersistence.conversation.ConversationContext;

import javax.persistence.EntityManager;


/**
 * Class responsible for keeping convesation State of the objects are maintain in EntityManager
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConversationStatelessContext extends ConversationContext {

    @Setter
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
