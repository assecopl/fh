package pl.fhframework.core.uc;

/**
 * Represents use case with single input parameter
 * @param <T> Input parameter's type
 * @param <C> Use case callback
 */
public interface IUseCaseOneInput<T, C extends IUseCaseOutputCallback> extends IUseCase<C> {
    /**
     * Main method used for use case run
     * @param one Input parameter
     */
    void start(T one);
}