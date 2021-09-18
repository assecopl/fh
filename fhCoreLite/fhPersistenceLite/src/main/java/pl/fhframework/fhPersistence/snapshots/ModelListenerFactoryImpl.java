package pl.fhframework.fhPersistence.snapshots;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.aspects.ApplicationContextHolder;
import pl.fhframework.aspects.snapshots.ModelListener;
import pl.fhframework.aspects.snapshots.ModelListenerFactory;
import pl.fhframework.fhPersistence.core.EntityManagerRepository;
import pl.fhframework.SessionManager;

/**
 */
@Component
public class ModelListenerFactoryImpl implements ModelListenerFactory {

    @Override
    public ModelListener getModelListener() {
        return  getModelListenerImpl();
    }

    public static ModelListener getModelListenerImpl() {
        if (ApplicationContextHolder.getApplicationContext() != null && SessionManager.getSession() != null
                && ApplicationContextHolder.getApplicationContext().getAutowireCapableBeanFactory().getBean(EntityManagerRepository.class).isConversation()) {
            return ApplicationContextHolder.getApplicationContext().getBean(ModelListener.class);
        }

        return null;
    }
}
