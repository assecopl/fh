package pl.fhframework.fhPersistence.snapshots;

import org.springframework.stereotype.Service;
import pl.fhframework.aspects.snapshots.ModelListener;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import javax.persistence.*;

/**
 * @author Paweł Ruta
 */
@Service
public class EntitySnapshotListener {

    @PrePersist
    void onPrePersist(Object o) {
        ModelListener modelListener = ModelListenerFactoryImpl.getModelListenerImpl();

        if (modelListener != null) {
            if (!modelListener.isRegisteredAsNewEntity(o)) {
                modelListener.registerNewEntity(o);

                modelListener.persistCalled(o);

                if (ISnapshotEnabled.class.isAssignableFrom(o.getClass())) {
                    if (modelListener != null) {
                        modelListener.persistCalledOnISnapshot(o);
                    }
                }
            }
        }
    }

    @PostPersist
    void onPostPersist(Object o) {
        ModelListener modelListener = ModelListenerFactoryImpl.getModelListenerImpl();

        if (modelListener != null) {
            modelListener.registerEntity(o);
        }
    }

    @PreUpdate
    void onPreUpdate(Object o) {
    }

    @PostUpdate
    void onPostUpdate(Object o) {
    }

    @PreRemove
    void onPreRemove(Object o) {
        ModelListener modelListener = ModelListenerFactoryImpl.getModelListenerImpl();
        if (modelListener != null) {
            modelListener.entityRemoved(o);
        }
        if (modelListener != null) {
            modelListener.removeDynamicCalled(o);
        }

        if (ISnapshotEnabled.class.isAssignableFrom(o.getClass())) {
            if (modelListener != null) {
                modelListener.removeCalledOnISnapshot(o);
            }
        }
    }

    @PostLoad
    void onPostLoad(Object o) {
        ModelListener modelListener = ModelListenerFactoryImpl.getModelListenerImpl();
        if (modelListener != null) {
            modelListener.registerEntity(o);
            modelListener.collectionRefreshed(o);
        }
    }
}
