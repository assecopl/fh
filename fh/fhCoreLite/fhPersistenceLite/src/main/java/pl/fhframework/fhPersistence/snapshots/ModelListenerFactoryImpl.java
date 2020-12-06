package pl.fhframework.fhPersistence.snapshots;


import org.springframework.stereotype.Component;
import pl.fhframework.SessionManager;
import pl.fhframework.aspects.ApplicationContextHolder;
import pl.fhframework.aspects.snapshots.ModelListener;
import pl.fhframework.aspects.snapshots.ModelListenerFactory;
import pl.fhframework.fhPersistence.conversation.ConversationManager;
import pl.fhframework.fhPersistence.core.EntityManagerRepository;

/**
 */
@Component
public class ModelListenerFactoryImpl implements ModelListenerFactory {

    @Override
    public ModelListener getModelListener() {
        return  getModelListenerImpl();
    }

    public static ModelListener getModelListenerImpl() {
        if (ApplicationContextHolder.getApplicationContext() != null && SessionManager.getSession() != null &&
                (!ApplicationContextHolder.getApplicationContext().getAutowireCapableBeanFactory().containsBean(EntityManagerRepository.beanName) ||
                ApplicationContextHolder.getApplicationContext().getAutowireCapableBeanFactory().getBean(EntityManagerRepository.class).isConversation()) &&
                ApplicationContextHolder.getApplicationContext().getAutowireCapableBeanFactory().getBean(ConversationManager.class).contextExits()) {
            return ApplicationContextHolder.getApplicationContext().getBean(ModelListener.class);
        }

        return null;
    }
}
