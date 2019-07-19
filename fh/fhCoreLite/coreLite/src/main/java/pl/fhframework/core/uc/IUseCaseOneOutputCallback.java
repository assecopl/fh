package pl.fhframework.core.uc;

import java.util.function.Consumer;

/**
 * Represents use case callback with single return value
 * @param <T> Output value type
 */
public interface IUseCaseOneOutputCallback<T> extends IUseCaseOutputCallback {
    /**
     * Outputs value to the parent use case
     * @param one Value to be outputted to parent use case
     */
    void output(T one);

    /**
     * Creates use case callback from provided {@link Consumer} instance
     * @param callbackMethod Instance of method implementing {@link Consumer}
     * @param <T> Output value type
     * @return Callback instance from provided parameters
     */
    static <T> IUseCaseOneOutputCallback<T> getCallback(Consumer<T> callbackMethod) {
        return callbackMethod::accept;
    }
}