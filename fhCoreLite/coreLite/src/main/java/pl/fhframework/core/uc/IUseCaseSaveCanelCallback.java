package pl.fhframework.core.uc;

import java.util.function.Consumer;

/**
 * Deprecated - misspelled class name. Use IUseCaseSaveCancelCallback instead.
 */
@Deprecated
public interface IUseCaseSaveCanelCallback<T> extends IUseCaseOutputCallback {

    void save(T one);

    void cancel();

    static <T> IUseCaseSaveCanelCallback<T> getCallback(Consumer<T> saveCallback, Runnable cancelCallback) {
        return new IUseCaseSaveCanelCallback<T>() {
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
}
