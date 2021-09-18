package pl.fhframework.core.uc;

/**
 * Represents use case with two input parameters
 * @param <T> First input parameter's type
 * @param <V> Second input parameter's type
 * @param <C> Use case callback
 */
public interface IUseCaseTwoInput<T, V, C extends IUseCaseOutputCallback> extends IUseCase<C> {
    /**
     * Main method used for use case run
     * @param one First input parameter
     * @param two Second input parameter
     */
    void start(T one, V two);
}