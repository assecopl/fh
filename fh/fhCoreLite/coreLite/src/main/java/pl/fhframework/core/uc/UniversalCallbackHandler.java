package pl.fhframework.core.uc;

import java.lang.reflect.Method;

/**
 * Universal callback handler. May be used instead of a real callback in special cases.
 */
@FunctionalInterface
public interface UniversalCallbackHandler extends IUseCaseOutputCallback {

    public void apply(Method method, Object[] args);
}