package pl.fhframework.core.uc;

import java.util.function.Consumer;

/**
 * Represents use case callback with two possible output methods
 * @param <T> Output parameter value type
 */
public interface IUseCaseSaveCancelCallback<T> extends IUseCaseOutputCallback {
    /**
     * Outputs callback to parent use case that processed object is ready to be saved or has been already saved
     * @param one Value that is ready to be saved or has been already saved
     */
    void save(T one);

    /**
     * Outputs callback to parent use that changes in processed object should be discarded or have been discarded
     */
    void cancel();

    /**
     * Creates use case callback from provided {@link Consumer} (save) and {@link Runnable} (cancel) instance
     * @param saveCallback Instance of method implementing {@link Consumer} for the save return way
     * @param cancelCallback Instance of method implementing {@link Runnable} for the cancel return way
     * @param <T> Output value type
     * @return Callback instance from provided parameters
     */
    static <T> IUseCaseSaveCancelCallback<T> getCallback(Consumer<T> saveCallback, Runnable cancelCallback) {
        return new IUseCaseSaveCancelCallback<T>() {
            @Override
            public void save(T one) {
                saveCallback.accept(one);
            }

            @Override
            public void cancel() {
                cancelCallback.run();
            }
        };
    }

    /**
     * Creates use case callback from provided {@link Consumer} (save) callback
     * @param saveCallback Instance of method implementing {@link Consumer} for the save return way
     * @param <T> Output value type
     * @return Callback instance from provided parameter
     */
    static <T> IUseCaseSaveCancelCallback<T> getCallback(Consumer<T> saveCallback) {
        return new IUseCaseSaveCancelCallback<T>() {
            @Override
            public void save(T one) {
                saveCallback.accept(one);
            }

            @Override
            public void cancel() {
            }
        };
    }
}
