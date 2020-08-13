package pl.fhframework.fhPersistence.snapshots;


import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.aspects.snapshots.ModelListener;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.FhConversationException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.datasource.StoreAccessService;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.fhPersistence.conversation.ConversationManager;
import pl.fhframework.fhPersistence.conversation.ConversationManagerUtils;
import pl.fhframework.fhPersistence.core.EntityManagerRepository;
import pl.fhframework.fhPersistence.core.model.ModelConfig;
import pl.fhframework.fhPersistence.core.model.ModelProxyService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 */
@Component
public class ModelListenerImpl implements ModelListener {

    @Autowired
    protected ConversationManagerUtils conversationManagerUtils;

    @Autowired(required = false)
    protected EntityManagerRepository emRepository;

    @Autowired
    protected ModelConfig modelConfig;

    @Autowired
    protected ModelProxyService modelProxyService;

    @Autowired
    protected StoreAccessService storeAccessService;

    @Override
    public void onInitialization(final Object target) {
        conversationManagerUtils.getConversationManager().unmarkInitializing((ISnapshotEnabled) target);
    }

    @Override
    public void onPreInitialization(Object target) {
        conversationManagerUtils.getConversationManager().markInitializing((ISnapshotEnabled) target);
    }

    @Override
    public void settingFieldCalled(final Object target, final Field field, final Class fieldType, final Object newValue) {
        if (isInitialized(target)) {
            if (getConversationManager().snapshotExists()) {
                addChange((ISnapshotEnabled) target, field, newValue);
            }
        }
    }

    @Override
    public void gettingFieldCalled(final Object target, final Field field, final Class fieldType) {
        if (isInitialized(target)) {
            if (getConversationManager().snapshotExists()) {
                if (Collection.class.isAssignableFrom(fieldType) || Map.class.isAssignableFrom(fieldType)) {
                    addChange((ISnapshotEnabled) target, field, null);
                }
            }
        }
    }

    @Override
    public void setMethodCalled(Object target, Method method, Class fieldType, Object newValue) {
        if (isInitialized(target) && isSnapshotEnabled(target)) {
            if (getConversationManager().snapshotExists()) {
                Method methodGet;
                try {
                    if (boolean.class == fieldType) {
                        methodGet = target.getClass().getMethod(method.getName().replaceFirst("set", "is"));
                    }
                    else {
                        methodGet = target.getClass().getMethod(method.getName().replaceFirst("set", "get"));
                    }
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("No method get", e);
                }
                try {
                    addChange((ISnapshotEnabled) target, methodGet.invoke(target), method, newValue);
                } catch (Exception e) {
                    throw new FhException("Unable to add value change", e);
                }
            }
        }
        modelProxyService.setAttributeValue((BaseEntity) target, StringUtils.firstLetterToLower(method.getName().substring(3)), newValue);
    }

    @Override
    public Object getMethodCalled(Object target, Method method, Class fieldType) {
        String fieldName = StringUtils.firstLetterToLower(boolean.class.isAssignableFrom(fieldType) ? method.getName().substring(2) : method.getName().substring(3));
        Object retVal = modelProxyService.getAttributeValue((BaseEntity) target, fieldName);
        if (isInitialized(target) && isSnapshotEnabled(target)) {
            if (getConversationManager().snapshotExists()) {
                if (Collection.class.isAssignableFrom(fieldType) || Map.class.isAssignableFrom(fieldType)) {
                    Method methodSet;
                    try {
                        if (boolean.class == fieldType) {
                            methodSet = target.getClass().getMethod(method.getName().replaceFirst("is", "set"), fieldType);
                        }
                        else {
                            methodSet = target.getClass().getMethod(method.getName().replaceFirst("get", "set"), fieldType);
                        }
                    } catch (NoSuchMethodException e) {
                        FhLogger.warn("Unavailable method set for {} - no snapshot record", method.toString());
                        return retVal;
                    }
                    addChange((ISnapshotEnabled) target, retVal, methodSet, null);
                }
            }
        }
        return retVal;
    }

    @Override
    public boolean hasDynamicFeatures(Object target) {
        return false;
    }

    @Override
    public boolean isDataModel(Object object) {
        return BaseEntity.class.isInstance(object);
    }

    @Override
    public void persistCalledOnISnapshot(final Object target) {
        if (isInitialized(target)) {
            if (getConversationManager().snapshotExists()) {
                getConversationManager().getCurrentSnapshot().persistCalled((ISnapshotEnabled) target);
            }
        }
    }

    @Override
    public void removeCalledOnISnapshot(final Object target) {
        if (isInitialized(target)) {
            if (getConversationManager().snapshotExists()) {
                getConversationManager().getCurrentSnapshot().removeCalled((ISnapshotEnabled) target);
            }
        }
    }

    @Override
    public void persistCalled(Object target) {
        if (BaseEntity.class.isInstance(target)) {
            modelProxyService.peristCalled((BaseEntity) target);
        }
    }

    @Override
    public void removeDynamicCalled(Object target) {
    }

    @Override
    public void removeStaticCalled(Object target) {
    }

    @Override
    public void collectionRefreshed(Object target) {
        // after refresh clear Collection proxy
        modelProxyService.refreshStaticCollection((BaseEntity) target, emRepository.getEntityManager());
    }

    @Override
    public void registerEntity(Object entity) {
        if (BaseEntity.class.isInstance(entity)) {
            modelProxyService.registerEntity((BaseEntity) entity);
        }
    }

    @Override
    public void registerNewEntity(Object entity) {
        if (BaseEntity.class.isInstance(entity)) {
            modelProxyService.registerNewEntity((BaseEntity) entity);
        }
    }

    @Override
    public boolean isRegisteredAsNewEntity(Object entity) {
        if (BaseEntity.class.isInstance(entity)) {
            return modelProxyService.isRegisteredAsNewEntity((BaseEntity) entity);
        }

        return false;
    }

    @Override
    public void entityRemoved(Object entity) {
        if (BaseEntity.class.isInstance(entity)) {
            modelProxyService.entityRemoved((BaseEntity) entity);
        }
    }

    @Override
    public void newDynamicObjectInit(Object target) {

    }

    public void exceptionOnLazy(Exception e) {
        if (HibernateException.class.isAssignableFrom(e.getClass())) {
            // todo: select on table with error ("select 1 from xxx")
            String dialect = (String) emRepository.getEntityManager().getEntityManagerFactory().getProperties().get("hibernate.dialect");
            if (dialect.contains("Derby")) {
                emRepository.getEntityManager().createNativeQuery("select 1 from SYSIBM.SYSDUMMY1").getSingleResult();
            }
            else {
                emRepository.getEntityManager().createNativeQuery("select 1").getSingleResult();
            }
        }
    }

    private void addChange(final ISnapshotEnabled target, final Field field, final Object newValue) {
        try {
            field.setAccessible(true);

            getConversationManager().getCurrentSnapshot().addChange(target, field, field.get(target), newValue);
        } catch (IllegalAccessException e) {
            throw new FhConversationException("Unable to read the setting value", e);
        }
    }

    private void addChange(final ISnapshotEnabled target, final Object oldValue, final Method methodSet, final Object newValue) {
        try {
            getConversationManager().getCurrentSnapshot().addChange(target, methodSet, oldValue, newValue);
        } catch (Exception e) {
            throw new RuntimeException("Unable to read the setting value", e);
        }
    }

    private boolean isInitialized(final Object target) {
        // can only be ISnapshotEnabled object.
        return target != null && getConversationManager() != null && getConversationManager().isInitialized((ISnapshotEnabled) target);
    }

    private boolean isSnapshotEnabled(final Object target) {
        // can only be ISnapshotEnabled object.
        return target != null && target instanceof ISnapshotEnabled;
    }

    private ConversationManager getConversationManager(){
        return conversationManagerUtils.getConversationManager();
    }
}
