package pl.fhframework.core.uc;

import pl.fhframework.UserSession;
import pl.fhframework.binding.IActionCallback;

import java.lang.reflect.Method;

/**
 * Interface of a form creator (normally this is a use case context). Extracted for better testing and loose coupling.
 */
public interface IFormUseCaseContext {

    boolean isSystemUseCase();

    boolean isTopStackUseCase();

    UserSession getUserSession();

    void runAction(String actionName, String formTypeId, Object... attributesValue);

    boolean runAction(Method method, Object methodTarget, String actionName, Object... attributesValue);

    /**
     * Runs an action callback instead of a standard and encouraged actions declared in FRM files and implemented as methods in use cases.
     *
     * @param callback callback called on a form component event
     */
    void runAction(IActionCallback callback);

    IUseCase<?> getUseCase();

    String getRemoteServerName();

    default IFormUseCaseContext getRealUseCaseContext() {
        return this;
    }
}
