package pl.fhframework.fhPersistence.core.model;

import lombok.Getter;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.FhException;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.fhPersistence.conversation.ConversationContext;
import pl.fhframework.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by pawel.ruta on 2017-02-22.
 *
 * This class can only be used by {@link ConversationContext} with method {@code
 * dynamicModelStore()}. !! Warning this class should not be injected anywhere. !!
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ModelStore {
    // <Memory address of original collection, Collection of ExtendedStaticObjects (Pojo)>
    protected Map<Integer, Collection<BaseEntity>> mapColFromStatObj = new HashMap<>();

    // <Memory address of collection of extended obj (pojo), original Collection>
    private Map<Integer, Collection<BaseEntity>> mapColFromExtObj = new HashMap<>();

    // key (qualified class name#id)
    protected Map<String, BaseEntity> entitiesMap = new HashMap<>();

    // <Memory address of entity, managed entities without id>
    @Getter
    protected Map<Integer, BaseEntity> newEntitiesMap = new HashMap<>();

    protected Set<BaseEntity> removedEntities = new HashSet<>();

    @Getter
    protected Set<BaseEntity> orphanedEntities = new HashSet<>();

    public static int getIdentityHashCode(Object obj) {
        Object realObject = getRealObject(obj);
        return System.identityHashCode(realObject);
    }

    public static Object getRealObject(Object obj) {
        if (HibernateProxy.class.isInstance(obj)) {
            //if (Hibernate.isInitialized(obj)) {
            return HibernateProxy.class.cast(obj).getHibernateLazyInitializer().getImplementation();
            //}
        }
        return obj;
    }

    public Collection<BaseEntity> getExtendedCollection(Collection<BaseEntity> collectionStatic) {
        return mapColFromStatObj.get(getIdentityHashCode(collectionStatic));
    }

    public Collection<Collection<BaseEntity>> getAllExtendedCollections() {
        return mapColFromStatObj.values();
    }

    public void addCollectionOfStaticObjects(Collection<BaseEntity> collectionExtended, Collection<BaseEntity> collectionStatic) {
        if (collectionExtended == null || collectionStatic == null) {
            throw new RuntimeException("Collection can't be null");
        }
        mapColFromStatObj.put(getIdentityHashCode(collectionStatic), collectionExtended);
        mapColFromExtObj.put(getIdentityHashCode(collectionExtended), collectionStatic);
    }

    public Collection<BaseEntity> getStaticCollection(Collection<BaseEntity> collectionExtended) {
        return mapColFromExtObj.get(getIdentityHashCode(collectionExtended));
    }

    public void clearStaticCollection(BaseEntity baseEntity, EntityManager entityManager) {
        try {
            for (Field field : ReflectionUtils.getFieldsWithHierarchy(baseEntity.getClass())) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    if (Collection.class.isAssignableFrom(field.getType()) && isLoaded(entityManager, baseEntity, field.getName())) {
                        field.setAccessible(true);
                        Collection<BaseEntity> collectionExtended = mapColFromStatObj.remove(getIdentityHashCode(field.get(baseEntity)));
                        mapColFromExtObj.remove(getIdentityHashCode(collectionExtended));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new FhException("Can't access entity field", e);
        }
    }

    public void registerEntity(BaseEntity entity) {
        newEntitiesMap.remove(getIdentityHashCode(entity));
        entitiesMap.put(getIdentityString(entity), entity);
    }

    public void registerNewEntity(BaseEntity entity) {
        newEntitiesMap.put(getIdentityHashCode(entity), entity);
    }

    public boolean isRegisteredAsNewEntity(BaseEntity entity) {
        return  newEntitiesMap.containsKey(getIdentityHashCode(entity));
    }

    public String getIdentityString(BaseEntity entity) {
        Class clazz = ModelConfig.getEntityClass(entity);
        return String.format("%s#%s", clazz.getName(), entity.getEntityId());
    }

    public void entityRemoved(BaseEntity entity) {
        removedEntities.add(entity);
    }

    public boolean isEntityMarkedRemoved(BaseEntity entity) {
        return removedEntities.contains(entity);
    }

    public BaseEntity getEntity(String identificationString) {
        return entitiesMap.get(identificationString);
    }

    public Collection<BaseEntity> getEntities() {
        return new LinkedList<>(entitiesMap.values());
    }

    public Set<BaseEntity> getRemovedEntities() {
        return new HashSet<>(removedEntities);
    }

    public void clearPersistentContext() {
        entitiesMap.clear();
        newEntitiesMap.clear();
        removedEntities.clear();
        orphanedEntities.clear();
    }

    private boolean isLoaded(EntityManager entityManager, BaseEntity entity, String name) {
        PersistenceUnitUtil persistUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        return persistUtil.isLoaded(entity, name);
    }
}
