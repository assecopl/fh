package pl.fhframework.aspects.conversation;

import java.lang.reflect.Method;

/**
 * @author Paweł Ruta
 */
public interface IUseCaseConversation {
    void subUsecaseStarted(Object owner);

    void usecaseStarted(Object owner, final Object[] daneWejsciowe);

    void usecaseEnded(Object owner);

    void usecaseTerminated(Object owner);

    void processAnnotationsBeforeAction(Method transition, Object owner);

    void processAnnotationsAfterAction(Method transition, Object owner);

    void registerOutputParams(Object owner, Object[] args);

    boolean isContextValid();
}
