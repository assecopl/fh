package pl.fhframework.aspects.snapshots;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 */
public interface ModelListener {
    void onPreInitialization(Object target);

    void onInitialization(final Object target);

    void settingFieldCalled(Object target, Field field, Class fieldType, final Object newValue);

    void gettingFieldCalled(Object target, Field field, Class fieldType);

    void setMethodCalled(Object target, Method method, Class fieldType, final Object newValue);

    Object getMethodCalled(Object target, Method method, Class fieldType);

    void persistCalledOnISnapshot(Object target);

    void removeCalledOnISnapshot(Object target);

    void persistCalled(Object target);

    void removeDynamicCalled(Object target);

    void removeStaticCalled(Object target);

    void exceptionOnLazy(Exception e);

    void newDynamicObjectInit(Object target);

    boolean hasDynamicFeatures(Object target);

    boolean isDataModel(Object target);

    void collectionRefreshed(Object target);

    void registerEntity(Object entity);

    void registerNewEntity(Object entity);

    boolean isRegisteredAsNewEntity(Object entity);

    void entityRemoved(Object entity);
}
