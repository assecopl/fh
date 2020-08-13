package pl.fhframework.core.session.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import pl.fhframework.core.FhException;
import pl.fhframework.Session;
import pl.fhframework.SessionManager;

/**
 * Scope of user session (not http session).
 */
public class SessionScope implements Scope {

    public static final String SESSION_SCOPE = "fhSessionScope";

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        SessionScopeBeanContainer container = getScopeContainer(name);
        synchronized (container) {
            if (container.getScopedObjects().containsKey(name)) {
                return container.getScopedObjects().get(name);
            } else {
                Object bean = objectFactory.getObject();
                container.getScopedObjects().put(name, bean);
                return bean;
            }
        }
    }

    @Override
    public Object remove(String name) {
        SessionScopeBeanContainer container = getScopeContainer(name);
        synchronized (container) {
            container.getDestructionCallbacks().remove(name);
            return container.getScopedObjects().remove(name);
        }
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        SessionScopeBeanContainer container = getScopeContainer(name);
        synchronized (container) {
            container.getDestructionCallbacks().put(name, callback);
        }
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }

    private SessionScopeBeanContainer getScopeContainer(String beanName) {
        Session session = SessionManager.getSession();
        if (session == null) {
            throw new FhException("Trying to autowire or remove '" + beanName + "' bean which is Session scoped, but there is not session provided by SessionManager");
        }
        return session.getScopeBeanContainer();
    }
}
