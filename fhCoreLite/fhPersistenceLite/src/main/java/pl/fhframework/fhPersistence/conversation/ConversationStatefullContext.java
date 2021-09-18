package pl.fhframework.fhPersistence.conversation;


import lombok.Getter;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;


/**
 * Class responsible for keeping convesation State of the objects are maintain in EntityManager
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConversationStatefullContext extends ConversationContext {

    @Getter
    @PersistenceContext(unitName = "FhPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
        return entityManager;
    }
}
