package pl.fhframework.fhPersistence.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import pl.fhframework.aspects.snapshots.SnapshotsModelAspect;
import pl.fhframework.core.FhConversationException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.datasource.StoreAccessService;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.core.rules.dynamic.model.dataaccess.Filter;
import pl.fhframework.core.rules.dynamic.model.dataaccess.From;
import pl.fhframework.core.rules.dynamic.model.predicates.CompareCondition;
import pl.fhframework.core.rules.dynamic.model.predicates.CompareOperatorEnum;
import pl.fhframework.core.rules.dynamic.model.predicates.ExistsInCondition;
import pl.fhframework.fhPersistence.conversation.ConversationManager;
import pl.fhframework.fhPersistence.conversation.ConversationManagerUtils;
import pl.fhframework.fhPersistence.core.EntityManagerRepository;
import pl.fhframework.ReflectionUtils;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Paviko on 2017-02-15.
 */
@Service
public class ModelProxyService {
    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected ModelConfig modelConfig;

    @Autowired
    protected EntityManagerRepository emRepository;

    @Autowired
    protected StoreAccessService storeAccessService;

    private static final ThreadLocal<Boolean> newExtendedPojoOrEntity = new ThreadLocal<>();

    private static final ThreadLocal<Queue<BaseEntity>> cascadePersistQueue = new ThreadLocal<>();

    public <T> T getAttributeValue(BaseEntity source, String fieldName) {
        return getAttributeValue(source, fieldName, true);
    }

    protected <T> T getAttributeValue(BaseEntity source, String fieldName, boolean create) {
        try {
            SnapshotsModelAspect.turnOff();

            PropertyAccessor propertyAccessor = PropertyAccessorFactory.forBeanPropertyAccess(source);

            T retVal = (T) propertyAccessor.getPropertyValue(fieldName);
            Class returnType = propertyAccessor.getPropertyType(fieldName);
            if (ReflectionUtils.isAssignablFrom(Collection.class, returnType)) {
                if (retVal == null) {
                    // todo: check type
                    if (ReflectionUtils.isAssignablFrom(Set.class, returnType)) {
                        retVal = (T) new LinkedHashSet<>();
                    }
                    else{
                        retVal = (T) new ArrayList();
                    }
                    propertyAccessor.setPropertyValue(fieldName, retVal);
                }
                retVal = (T) getCollOfStatProxy((Collection<BaseEntity>) retVal, source, fieldName);
            }
            return retVal;
        } finally {
            SnapshotsModelAspect.turnOn();
        }
    }

    public void setAttributeValue(BaseEntity target, String fieldName, Object newValue) {
        setAttributeValue(target, fieldName, newValue, true);
    }

    public void setAttributeValue(BaseEntity target, String fieldName, Object newValue, boolean processRelations) {
        Object oldValue = getAttributeValue(target, fieldName);

        PropertyAccessor propertyAccessor = PropertyAccessorFactory.forBeanPropertyAccess(target);
        try {
            SnapshotsModelAspect.turnOff();

            propertyAccessor.setPropertyValue(fieldName, newValue);
        } finally {
            SnapshotsModelAspect.turnOn();
        }
        if (processRelations) {
            processRelations(target, oldValue, newValue, fieldName);
        }
        return;
    }

    protected void processRelations(BaseEntity target, Object oldValue, Object newValue, String fieldName) {
        String otherSideFieldName = modelConfig.getBiDirectionalFieldName(ModelConfig.getEntityClass(target), fieldName);
        if (otherSideFieldName == null) {
            otherSideFieldName = modelConfig.getNonOwningField(ModelConfig.getEntityClass(target), fieldName);
        }
        if (otherSideFieldName != null) {
            if (oldValue instanceof Collection || newValue instanceof Collection) {
                if (oldValue != null) {
                    for (BaseEntity entity : (Collection<BaseEntity>) oldValue) {
                        changeRelation(entity, null, entity, null, target, otherSideFieldName);
                    }
                }
                if (newValue != null) {
                    for (BaseEntity entity : (Collection<BaseEntity>) newValue) {
                        changeRelation(null, entity, null, entity, target, otherSideFieldName);
                    }
                }
            } else {
                Object oldValueOtherSide = null;
                Object newValueOtherSide = null;
                if (oldValue != null) {
                    oldValueOtherSide = getAttributeValue((BaseEntity) oldValue, otherSideFieldName);
                }
                newValue = getPojo((BaseEntity) newValue);
                if (newValue != null) {
                    newValueOtherSide = getAttributeValue((BaseEntity) newValue, otherSideFieldName);
                }
                changeRelation(oldValue, newValue, oldValueOtherSide, newValueOtherSide, target, otherSideFieldName);
            }
        }
    }

    public BaseEntity getPojo(BaseEntity entity) {
         return entity;
    }

    protected void changeRelation(Object oldValue, Object newValue, Object oldValueOtherSide, Object newValueOtherSide, BaseEntity target, String otherSideFieldName) {
        if (oldValue == newValue) {
            return;
        }
        if (oldValue != null) {
            if (Collection.class.isInstance(oldValueOtherSide)) {
                Collection collection = (Collection) ReflectionUtils.getRealObject(oldValueOtherSide);
                collection.remove(target);
            } else if (!Map.class.isInstance(oldValueOtherSide)){
                setAttributeValue((BaseEntity) oldValue, otherSideFieldName, null, false);
            }
        }
        if (newValue != null) {
            if (Collection.class.isInstance(newValueOtherSide)) {
                Collection collection = (Collection) ReflectionUtils.getRealObject(newValueOtherSide);
                if (!collection.contains(target)) {
                    collection.add(target);
                }
            } else if (!Map.class.isInstance(newValueOtherSide)) {
                setAttributeValue((BaseEntity) newValue, otherSideFieldName, target, false);
            }
        }
    }

    public void clearOtherSideRelations(BaseEntity pojoEntity) {
        Map<Class, Set<String>> otherSideRelations = modelConfig.getOtherSideRelations(ModelConfig.getEntityClass(pojoEntity));
        otherSideRelations.forEach((aClass, fieldsName) -> {
            if (modelConfig.isPersistent(aClass)) {
                clearOtherSideRelations(aClass, fieldsName, pojoEntity);
            }
        });

    }

    protected void clearOtherSideRelations(Class aClass, Set<String> fieldsName, BaseEntity pojoEntity) {
        for (String fieldName : fieldsName) {
            Collection<BaseEntity> entitiesList = Collections.emptyList();
            String relationName = modelConfig.getBiDirectionalFieldName(aClass, fieldName);
            // deleting object (pojoEntity) will remove
            if (relationName != null && modelConfig.getCascadeRemoveFields(ModelConfig.getEntityClass(pojoEntity)).contains(relationName)) {
                continue;
            }
            if (!modelConfig.isNonOwningField(aClass, fieldName)) {
                if (Collection.class.isAssignableFrom(ReflectionUtils.getFieldType(aClass, fieldName))) {
                    ExistsInCondition eic = ExistsInCondition.of("row." + fieldName, "child", CompareCondition.of("child.id", CompareOperatorEnum.Equal, pojoEntity.getEntityId()));
                    From from = From.of(aClass, "row", Filter.of(eic));
                    entitiesList = storeAccessService.storeFind(from);
                } else {
                    From from = From.of(aClass, "row", Filter.of(CompareCondition.of("row." + fieldName + ".id", CompareOperatorEnum.Equal, pojoEntity.getEntityId())));
                    entitiesList = storeAccessService.storeFind(from);
                }
            }

            Boolean isfieldCollection = null;
            for (BaseEntity pojo : entitiesList) {
                PropertyAccessor propertyAccessor = PropertyAccessorFactory.forBeanPropertyAccess(pojo);
                if (propertyAccessor.isReadableProperty(fieldName)) {
                    if (isfieldCollection == null) {
                        isfieldCollection = Collection.class.isAssignableFrom(propertyAccessor.getPropertyType(fieldName));
                    }

                    Object value = getAttributeValue(pojo, fieldName, false);

                    if (isfieldCollection) {
                        Collection collection = (Collection) value;
                        if (collection != null) {
                            ReflectionUtils.getRealObject(collection).remove(pojoEntity);
                            //collection.remove(pojoEntity); // owner attribute should be removed
                        }
                    } else if (value == pojoEntity) {
                        setAttributeValue(pojo, fieldName, null, false);
                    }
                }
            }
        }
    }

    public void peristCalled(BaseEntity target) {
    }

    protected Collection<BaseEntity> getCollOfStatProxy(Collection<BaseEntity> collectionStatic, BaseEntity owner, String fieldName) {
        if (collectionStatic == null) {
            return null;
        }

        Collection<BaseEntity> collection = getModelStore().getExtendedCollection(collectionStatic);
        if (collection == null) {
            collection = createNewCollection(ReflectionUtils.getRealClass(collectionStatic));

            for (BaseEntity staticEntity : collectionStatic) {
                collection.add(staticEntity);
            }
            collection = getProxyCollection(collection, owner, fieldName);
            getModelStore().addCollectionOfStaticObjects(collection, collectionStatic);
        }

        return  collection;
    }

    protected Collection createNewCollection(Type collType) {
        if (ReflectionUtils.isAssignablFrom(Set.class, collType)) {
            return new LinkedHashSet<>();
        }
        else {
            return new ArrayList<>();
        }
    }

    protected Collection<BaseEntity> getProxyCollection(Collection<BaseEntity> collection, BaseEntity owner, String fieldName) {
        String otherSideFieldName = modelConfig.getBiDirectionalFieldName(ModelConfig.getEntityClass(owner), fieldName);
        if (otherSideFieldName == null) {
            otherSideFieldName = modelConfig.getNonOwningField(ModelConfig.getEntityClass(owner), fieldName);
        }
        if (otherSideFieldName != null) {
            ProxyFactory factory = new ProxyFactory();
            factory.setTarget(collection);
            factory.setProxyTargetClass(true);
            Pointcut pc = new CollectionModificationPointcut();
            Advice advice = new CollectionModificationInterceptor(owner, fieldName, otherSideFieldName);
            Advisor advisor = new DefaultPointcutAdvisor(pc, advice);

            factory.addAdvisor(advisor);

            return (Collection<BaseEntity>) factory.getProxy();
        }

        return collection;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    protected class CollectionModificationInterceptor implements MethodInterceptor {
        private BaseEntity owner;

        private String fieldName;

        private String otherSideFieldName;

        public Object invoke(MethodInvocation invocation) throws Throwable {
            Collection elements = new ArrayList((Collection) invocation.getThis());

            Object retVal = invocation.proceed();

            String methodName = invocation.getMethod().getName();
            if ("add".equals(methodName) && Boolean.TRUE.equals(retVal)) {
                addedElement((Collection) invocation.getThis(), invocation.getArguments()[0]);
            } else if ("remove".equals(methodName) && Boolean.TRUE.equals(retVal)) {
                removedElement((Collection) invocation.getThis(), invocation.getArguments()[0]);
            } else if ("addAll".equals(methodName) && Boolean.TRUE.equals(retVal)) {
                ((Collection) invocation.getArguments()[0]).forEach(element ->
                        addedElement((Collection) invocation.getThis(), element));
            } else if ("removeAll".equals(methodName) && Boolean.TRUE.equals(retVal)) {
                ((Collection) invocation.getArguments()[0]).forEach(element ->
                        removedElement((Collection) invocation.getThis(), element));
            } else if ("clear".equals(methodName)) {
                elements.forEach(element ->
                        removedElement((Collection) invocation.getThis(), element));
            }
            return retVal;
        }

        private void addedElement(Collection collection, Object element) {
            Object valueOtherSide = getAttributeValue((BaseEntity) element, otherSideFieldName);
            if (Collection.class.isInstance(valueOtherSide)) {
                Collection collectionOtherSide = (Collection) ReflectionUtils.getRealObject(valueOtherSide);
                if (!collectionOtherSide.contains(owner)) {
                    collectionOtherSide.add(owner);
                }
            } else {
                // old owner will be processed by changeRelation
                setAttributeValue((BaseEntity) element, otherSideFieldName, owner);
            }
            if (modelConfig.isOrphanRemoval(ModelConfig.getEntityClass(owner), fieldName)) {
                getModelStore().getOrphanedEntities().remove(element);
            }
        }

        private void removedElement(Collection collection, Object element) {
            Object valueOtherSide = getAttributeValue((BaseEntity) element, otherSideFieldName);
            if (Collection.class.isInstance(valueOtherSide)) {
                Collection collectionOtherSide = (Collection) ReflectionUtils.getRealObject(valueOtherSide);
                collectionOtherSide.remove(owner);
            } else {
                setAttributeValue((BaseEntity) element, otherSideFieldName, null);
            }

            if (modelConfig.isOrphanRemoval(ModelConfig.getEntityClass(owner), fieldName)) {
                getModelStore().getOrphanedEntities().add((BaseEntity) element);
            }
        }
    }

    protected class CollectionModificationPointcut extends StaticMethodMatcherPointcut {
        public boolean matches(Method method, Class cls) {
            return ("add".equals(method.getName()) || "addAll".equals(method.getName()) ||
                    "remove".equals(method.getName()) || "removeAll".equals(method.getName()) ||
                    "clear".equals(method.getName()));
        }
    }

    public void refreshStaticCollection(BaseEntity baseEntity, EntityManager entityManager) {
        if (entityManager.contains(baseEntity)) {
            getModelStore().clearStaticCollection(baseEntity, entityManager);
        }
    }

    protected Class getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unknown class", e);
        }
    }

    /**
     * There can be javassist class. Return proper class
     *
     * @param input object
     * @return class of object
     */
    protected Class getClass(final Object input) {
        if (input != null) {
            if (input.getClass().getName().contains("_$$_")) {
                return input.getClass().getSuperclass();
            }
            return input.getClass();
        }

        return null;
    }

    protected void syncStaticOrHybridCollection(Collection<BaseEntity> extColl) {
        ModelStore modelStore = getModelStore();
        Collection<BaseEntity> staticColl = modelStore.getStaticCollection(extColl);

        if (staticColl != null) {
            staticColl.clear();
            extColl.forEach(staticColl::add);
        }
    }

    public void syncObjectCollections(BaseEntity baseEntity) {
        Class staticClass = baseEntity.getClass();
        ((Map<String, ModelConfig.FieldAnnotation>)modelConfig.getClassFieldsAnnotationsForClass(staticClass)).forEach((fieldName, fieldAnnotation) -> {
            if (OneToMany.class.isAssignableFrom(fieldAnnotation.getRelation().getClass()) || ManyToMany.class.isAssignableFrom(fieldAnnotation.getRelation().getClass())) {
                fieldAnnotation.getField().setAccessible(true);
                try {
                    Collection<BaseEntity> collection = (Collection<BaseEntity>) ReflectionUtils.findGetter(baseEntity.getClass(), fieldAnnotation.getField()).get().invoke(baseEntity);
                    syncStaticOrHybridCollection(collection);
                } catch (Exception e) {
                    throw new FhException(String.format("Can't access field %s in %s", fieldName, baseEntity.getClass().getSimpleName()), e);
                }
            }
        });
    }

    public void sychronizeCollections() {
        getModelStore().getAllExtendedCollections().forEach(this::syncStaticOrHybridCollection);

        // process orphan removal
        getModelStore().getOrphanedEntities().forEach(entity -> {
            if (!getModelStore().isEntityMarkedRemoved(entity)) {
                storeAccessService.storeDelete(entity);
            }
        });
    }

    public ModelStore getModelStore() {
        return getConversationManager().getCurrentContext().getModelStore();
    }

    protected ConversationManager getConversationManager() {
        // Service / dao can be called without web context (eg. Timer), then would be exception on @Autowired,
        // because ConversationManager bean have Session scope.
        return applicationContext.getBean(ConversationManagerUtils.class).getConversationManager();
    }

    public void registerEntity(BaseEntity entity) {
        getModelStore().registerEntity(entity);
    }

    public void registerNewEntity(BaseEntity entity) {
        getModelStore().registerNewEntity(entity);
    }

    public boolean isRegisteredAsNewEntity(BaseEntity entity) {
        return getModelStore().isRegisteredAsNewEntity(entity);
    }

    public void entityRemoved(BaseEntity entity) {
        getModelStore().entityRemoved(entity);
    }

    public Set<BaseEntity> getRemovedEntities() {
        return getModelStore().getRemovedEntities();
    }

    public BaseEntity getNewEntity(BaseEntity pojoOrEntity, EntityManager em, ModelStore prevStore, ModelStore currentStore) {
        return (BaseEntity) em.find(getClass(pojoOrEntity), pojoOrEntity.getEntityId());
    }

    public void detachRemovedEntities(ModelStore currentDMS, ModelStore prevDMS, EntityManager entityManager) {
        currentDMS.getRemovedEntities().forEach(entity -> {
            BaseEntity baseEntity = prevDMS.getEntity(currentDMS.getIdentityString(entity));
            if (baseEntity != null) {
                entityManager.detach(baseEntity);
            }
        });
    }

    public void refresh(List<BaseEntity> toRefresh, EntityManager entityManager) {
        // todo: check if modified entity before refresh
        RefreshContext context = getRefreshContext();
        context.setModelStore(getModelStore());
        context.setEntityManager(entityManager);

        List<BaseEntity> toRemove = new LinkedList<>();

        toRefresh.forEach(inputEntity -> {
            BaseEntity entity = refreshEntity(inputEntity, false, false, context, true);
            if (context.getRemovedEntites().contains(context.getModelStore().getIdentityString(entity))) {
                toRemove.add(entity);
            }
        });

        toRefresh.removeAll(toRemove);

        context.getToDetach().forEach(entity -> {
            if (entityManager.contains(entity)) {
                entityManager.detach(entity);
            }
        });

        context.getToRefresh().forEach(entity -> {
            // todo: collection to clear from context
            refreshStaticCollection(entity, entityManager);
            if (!entityManager.contains(entity)) {
                throw new FhConversationException("Incorrect use of business transactions. Not persisted object passed to new calle transaction, saved and then reused in caller transaction.");
            }
            entityManager.refresh(entity);
        });
    }

    protected RefreshContext getRefreshContext() {
        return new RefreshContext();
    }

    /**
     * Prepare for entity refresh:
     * 1. If object doesn't exist in database it will be mark to be detached, but first check associations.
     * If association exists then set it null or remove from collection, so it won't be detached
     * 2. If object exists in database then check associations. If associated entity doesn't exists then set it null or
     * remove from collection, otherwise hibernate will throw exception.
     *
     * @return if entity exists
     */
    protected BaseEntity refreshEntity(BaseEntity inputEntity, boolean willBeDetached, boolean willBeRefreshed, RefreshContext context, boolean cascade) {
        BaseEntity entity = inputEntity;

        if (context.getRefreshedEntities().contains(context.getModelStore().getIdentityHashCode(entity))) {
            return entity;
        }

        context.getRefreshedEntities().add(context.getModelStore().getIdentityHashCode(entity));

        if (!exists(entity, ModelConfig.getEntityClass(entity), context.getEntityManager())) {
            context.getRemovedEntites().add(context.getModelStore().getIdentityString(entity));
            if (!willBeDetached && cascade) {
                context.getToDetach().add(entity);
            }
            willBeDetached = true;
            willBeRefreshed = false;
        } else if (!willBeRefreshed) {
            if (cascade) {
                context.getToRefresh().add(entity);
            }
            willBeRefreshed = true;
            willBeDetached = false;
        }

        refreshStaticEntity(entity, willBeDetached, willBeRefreshed, context);

        return entity;
    }

    protected void refreshStaticEntity(BaseEntity entity, boolean willBeDetached, boolean willBeRefreshed, RefreshContext context) {
        // todo: doesn't support calculated properties?
        List<Field> fields = ReflectionUtils.getFieldsWithHierarchy(entity.getClass());
        fields.forEach(field -> {
            if (!Modifier.isStatic(field.getModifiers())) {
                if (isLoaded(context.getEntityManager(), entity, field.getName())) {
                    field.setAccessible(true);
                    try {
                        // todo: check if cascade refresh
                        if (BaseEntity.class.isAssignableFrom(field.getType())) {
                            BaseEntity associatedEntity = (BaseEntity) field.get(entity);
                            if (associatedEntity != null) {
                                BaseEntity underlying = associatedEntity;
                                refreshEntity(underlying, willBeDetached, willBeRefreshed, context, false);

                                if (context.getRemovedEntites().contains(context.getModelStore().getIdentityString(associatedEntity)) && !willBeDetached ||
                                        !context.getRemovedEntites().contains(context.getModelStore().getIdentityString(associatedEntity)) && willBeDetached) {
                                    field.set(entity, null);
                                }
                            }
                        } else if (Collection.class.isAssignableFrom(field.getType())) {
                            Class collectionType = ReflectionUtils.getGenericArgumentsRawClasses(field.getGenericType())[0];
                            if (BaseEntity.class.isAssignableFrom(collectionType)) {
                                Collection<BaseEntity> collection = (Collection<BaseEntity>) field.get(entity);
                                if (collection != null) {
                                    Collection<BaseEntity> toRemove = new LinkedList<>();
                                    collection.forEach(element -> {
                                        BaseEntity underlying = element;
                                        refreshEntity(underlying, willBeDetached, willBeRefreshed, context, false);

                                        if (context.getRemovedEntites().contains(context.getModelStore().getIdentityString(element)) && !willBeDetached ||
                                                !context.getRemovedEntites().contains(context.getModelStore().getIdentityString(element)) && willBeDetached) {
                                            toRemove.add(element);
                                        }
                                    });
                                    collection.removeAll(toRemove);
                                }
                            }
                        }
                    } catch (IllegalAccessException iae) {
                        throw new FhException("Can't access entity field", iae);
                    }
                }
            }
        });
    }

    protected boolean isLoaded(EntityManager entityManager, BaseEntity entity, String name) {
        PersistenceUnitUtil persistUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        return persistUtil.isLoaded(entity, name);
    }

    public boolean exists(final BaseEntity entity, final Class<? extends BaseEntity> entityClass, EntityManager entityManager) {
        if (entity.getEntityId() == null) {
            return false;
        }

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<? extends BaseEntity> from = cq.from(entityClass);

        cq.select(cb.count(from));
        // todo: get id attribute name from annotations @Id @EmbeddedId ...
        cq.where(cb.equal(from.get("id"), entity.getEntityId()));

        final TypedQuery<Long> tq = entityManager.createQuery(cq);
        return tq.getSingleResult() > 0;
    }

    public boolean isNew(final BaseEntity entity) {
        if (entity.getEntityId() == null) {
            return true;
        }

        return false;
    }


    public Queue<BaseEntity> getEntitiesToCascadePersist() {
        if (cascadePersistQueue.get() == null) {
            return new LinkedList<>();
        }
        return cascadePersistQueue.get();
    }

    public void clearEntitiesToCascadePersist() {
        if (cascadePersistQueue.get() != null) {
            cascadePersistQueue.get().clear();
        }
    }

    @Getter
    @Setter
    protected class RefreshContext {
        private Set<String> removedEntites = new HashSet<>();
        private Set<Integer> refreshedEntities = new HashSet<>();
        private Set<BaseEntity> toDetach = new HashSet<>();
        private Set<BaseEntity> toRefresh = new HashSet<>();

        private ModelStore modelStore;
        private EntityManager entityManager;
    }
}
