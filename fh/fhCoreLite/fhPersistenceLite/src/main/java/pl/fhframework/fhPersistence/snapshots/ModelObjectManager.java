package pl.fhframework.fhPersistence.snapshots;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.aspects.snapshots.model.IUnmanagedUseCaseParameter;
import pl.fhframework.core.FhConversationException;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.fhPersistence.conversation.ConversationContext;
import pl.fhframework.fhPersistence.core.model.ModelProxyService;
import pl.fhframework.fhPersistence.core.model.ModelStore;
import pl.fhframework.fhPersistence.snapshots.model.*;
import pl.fhframework.ReflectionUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 */
@Component
public class ModelObjectManager {

    private static final Class<?> ARRAYS_AS_LIST = Arrays.asList().getClass();

    @Autowired
    private ModelProxyService modelProxyService;

    public void createSnapshot(final Object owner, final ConversationContext cc) {
        cc.getSnapshotsStack().push(new Snapshot(owner));
    }

    public void dropSnapshot(final Object owner, final ConversationContext cc) {
        Snapshot snapshot = popSnapshot(owner, cc);
        if (snapshot.getSnapshotEntries().size() > 0 && !cc.getSnapshotsStack().empty()) {
            Snapshot lowerSnapshot = cc.getSnapshotsStack().peek();
            for (Map.Entry<Integer, SnapshotObject> toDrop : snapshot.getSnapshotEntries().entrySet()) {
                if (lowerSnapshot.getSnapshotEntries().containsKey(toDrop.getKey())) {
                    SnapshotObject lowerEntry = lowerSnapshot.getSnapshotEntries().get(toDrop.getKey());
                    for (Map.Entry<AccessibleObject, SnapshotEntry> toDropValue : toDrop.getValue().getChangedValues().entrySet()) {
                        if (!lowerEntry.getChangedValues().containsKey(toDropValue.getKey())) {
                            lowerEntry.getChangedValues().put(toDropValue.getKey(), toDropValue.getValue());
                        }
                    }
                } else {
                    lowerSnapshot.getSnapshotEntries().put(toDrop.getKey(), toDrop.getValue());
                }
            }
        }
    }

    public void restoreSnapshot(final Object owner, final ConversationContext cc) {
        Snapshot snapshot = peekSnapshot(owner, cc);
        snapshot.setSuspended(true);

        // If object is persisted manually, we have to remove them form EntityManager, but we have to keep the order of
        // cascades (especially orphaned elements)

        // 1. Find objects that where persisted manually. Remember objects with orphanRemoval flag aside.
        // 1a. If object was removed then call persist (if flush was called manually after remove, then programmer has to
        // deal yourself with this situation - call merge and eventually change the reference of object in whole graph)
        Map<Integer, SnapshotObject> persisted = new LinkedHashMap<>();
        Map<Integer, SnapshotObject> persistedOrphans = new LinkedHashMap<>();
        EntityManager em = cc.getEntityManager();
        for (SnapshotObject entry : snapshot.getSnapshotEntries().values()) {
            if (entry.getState() == SnapshotEmObjectState.Persisted && em.contains(entry.getTarget())) {
                if (entry.isOrphan()) {
                    persistedOrphans.put(ModelStore.getIdentityHashCode(entry.getTarget()), entry);
                } else {
                    persisted.put(ModelStore.getIdentityHashCode(entry.getTarget()), entry);
                }
            } else if (entry.getState() == SnapshotEmObjectState.Removed) {
                em.persist(entry.getTarget());
            }
        }

        // 2. If object is manually persisted, then leave changed (not restored) all its fields that are objects with
        // orphanRemoval flag set true
        for (SnapshotObject entry : snapshot.getSnapshotEntries().values()) {
            for (AccessibleObject ao : entry.getChangedValues().keySet()) {
                if (ao instanceof Field) {
                    Field field = (Field) ao;
                    if (entry.getState() == SnapshotEmObjectState.Persisted) {
                        if (ISnapshotEnabled.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            try {
                                ISnapshotEnabled newValue = (ISnapshotEnabled) field.get(entry.getTarget());
                                if (newValue != null && persistedOrphans.containsKey(ModelStore.getIdentityHashCode(newValue))) {
                                    continue;
                                }
                            } catch (IllegalAccessException e) {
                                // missing dependency to FhLogger
                                LoggerFactory.getLogger(ModelObjectManager.class).error(e.getMessage(), e);
                            }
                        }
                    }
                }
                entry.restore(ao);
            }
        }

        // 3. Remove all nonorphan objects from "persisted" map, keeping track of orphan cascade. Then remove objects
        // from persistedOrphans (also keeping track of orphan cascade)
        cascadeRemove(persisted, persistedOrphans, em);

        snapshot.clear();
    }

    public boolean isEntityModified(final BaseEntity entity, final ConversationContext cc) {
        if (entity instanceof ISnapshotEnabled) {
            for (Snapshot snapshot : cc.getSnapshotsStack()) {
                if (snapshot.isModified()) {
                    return true;
                }
            }

            return false;
        }

        // todo: add annotation
        throw new FhConversationException(" No entity change service without ISnapshotEnabled interface");
    }

    public void clearExchangedObjects(final ConversationContext prevContext, final ConversationContext currentContext) {
        //modelProxyService.clearEmptyCollections(prevContext.getModelStore());
        //modelProxyService.detachRemovedEntities(currentContext.getModelStore(), prevContext.getModelStore(), prevContext.getEntityManager());

        ExchangedEntities exchangedEntities = currentContext.getExchangedEntities();
        currentContext.overLoadDynamicModelStore(prevContext.getModelStore());
        try {
            modelProxyService.refresh(exchangedEntities.getEntries().stream().map(ExchangedEntityEntry::getPrevEntity).collect(Collectors.toList()),
                    prevContext.getEntityManager());
        } finally {
            currentContext.overLoadDynamicModelStore(null);
        }
    }

    public <T> T exchangeManagedObjects(final T input, final ConversationContext prevContext,
                                        final ConversationContext currentContext, final boolean inputExchange) {

        if (input instanceof IUnmanagedUseCaseParameter) {
            return input;
        }

        if (Collection.class.isInstance(input)) {
            Collection collection = Collection.class.cast(input);
            Collection copy;
            if (collection.getClass() == ARRAYS_AS_LIST) {
                copy = new ArrayList<>();
            } else {
                copy = ReflectionUtils.newInstance(collection.getClass());
            }
            collection.forEach(element -> copy.add(exchangeManagedObjects(element, prevContext, currentContext, inputExchange)));
            return (T) copy;
        }
        else if (input instanceof BaseEntity) {
            return (T) exchangeEntity((BaseEntity) input, prevContext, currentContext, inputExchange);
        } else if (input != null) {
            exchangeWrappedManagedObjects(input, prevContext, currentContext, inputExchange);
        }

        return input;
    }

    protected BaseEntity exchangeEntity(BaseEntity input, ConversationContext prevContext, ConversationContext currentContext, boolean inputExchange) {
        BaseEntity newInput = null;

        if (input.getEntityId() != null){
            // only if managed
            if (prevContext.getEntityManager().contains(input)) {
                newInput = getNewEntity(input, currentContext.getEntityManager(), prevContext, currentContext);
            }
            else {
                newInput = input;
            }
            if (newInput == null) {
                throw new FhConversationException("Not commited new persisted object passed to new conversation");
            }
        }

        if (newInput != null) {
            if (inputExchange) {
                currentContext.getExchangedEntities()
                        .getEntries().add(new ExchangedEntityEntry(input, newInput));
            }
            else {
                prevContext.getExchangedEntities().getEntries().add(new ExchangedEntityEntry(newInput, input));
            }
            return newInput;
        }

        return input;
    }


    private void cascadeRemove(final Map<Integer, SnapshotObject> persisted,
                               final Map<Integer, SnapshotObject> persistedOrphans, final EntityManager entityManager) {
        cascadeRemoveRecursive(persisted, persistedOrphans, entityManager);
        cascadeRemoveRecursive(persistedOrphans, persistedOrphans, entityManager);
    }

    private void cascadeRemoveRecursive(final Map<Integer, SnapshotObject> toRemove,
                                        final Map<Integer, SnapshotObject> toTrack, final EntityManager entityManager) {
        for (Map.Entry<Integer, SnapshotObject> entry : new HashSet<>(toRemove.entrySet())) {
            if (toRemove.remove(entry.getKey()) != null) {
                entityManager.remove(entry.getValue().getTarget());
                recursiveTrackRemove(entry.getValue(), toTrack);
            }
        }
    }

    private void recursiveTrackRemove(final SnapshotObject value, final Map<Integer, SnapshotObject> toTrack) {
        for (AccessibleObject ao : value.getChangedValues().keySet()) {
            if (ao instanceof Field) {
                Field field = (Field) ao;
                if (ISnapshotEnabled.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    try {
                        ISnapshotEnabled newValue = (ISnapshotEnabled) field.get(value.getTarget());
                        if (newValue != null) {
                            SnapshotObject orphan = toTrack.remove(ModelStore.getIdentityHashCode(newValue));
                            if (orphan != null) {
                                recursiveTrackRemove(orphan, toTrack);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        // missing dependency to FhLogger
                        LoggerFactory.getLogger(ModelObjectManager.class).error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    private Snapshot peekSnapshot(final Object owner, final ConversationContext cc) {
        Snapshot snapshot = cc.getSnapshotsStack().peek();
        if (!snapshot.isOwner(owner)) {
            throw new IllegalStateException(
                    "Incorrect use of snapshot stacks, expected: ".concat(snapshot.getOwner().toString()).concat(", got: ")
                            .concat(owner.toString()));
        }

        return snapshot;
    }

    private Snapshot popSnapshot(final Object owner, final ConversationContext cc) {
        Snapshot snapshot = cc.getSnapshotsStack().pop();
        if (!snapshot.isOwner(owner)) {
            throw new IllegalStateException(
                    "Incorrect use of snapshot stacks, expected: ".concat(snapshot.getOwner().toString()).concat(", got: ")
                            .concat(owner.toString()));
        }

        return snapshot;
    }

    private void exchangeWrappedManagedObjects(final Object input, final ConversationContext prevContext, ConversationContext currentContext, boolean inputExchange) {
        Class klasa = input.getClass();

        EntityManager em = currentContext.getEntityManager();

        try {
            while (!klasa.equals(Object.class)) {
                for (Field field : klasa.getDeclaredFields()) {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Class collectionType = ReflectionUtils.getGenericArgumentsRawClasses(field.getGenericType())[0];
                        if (BaseEntity.class.isAssignableFrom(collectionType)) {
                            field.setAccessible(true);
                            Collection collection = (Collection) field.get(input);
                            if (collection != null) {
                                List newCollection = new ArrayList<>(collection.size());
                                collection.forEach(element -> newCollection.add(exchangeEntity((BaseEntity) element, prevContext, currentContext, inputExchange)));
                                field.set(input, newCollection);
                            }
                        }
                    }
                    else if (BaseEntity.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);

                        BaseEntity entity = (BaseEntity) field.get(input);
                        if (entity != null) {
                            BaseEntity newEntity = exchangeEntity(entity, prevContext, currentContext, inputExchange);
                            field.set(input, newEntity);
                        }
                    }
                }
                klasa = klasa.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            throw new FhConversationException("Entity field access error", e);
        }
    }

    protected BaseEntity getNewEntity(final BaseEntity entity, final EntityManager em, ConversationContext prevContext, ConversationContext currentContext) {
        return modelProxyService.getNewEntity(entity, em, prevContext.getModelStore(), currentContext.getModelStore());
    }

    public void sychronizeCollections() {
        modelProxyService.sychronizeCollections();
    }

    public void manageNonOwningRelations() {
    }

    public void managePersistenceSession() {
        sychronizeCollections();
        manageNonOwningRelations();
    }
}
