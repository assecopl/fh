package pl.fhframework.core.uc;

/**
 * Represents use case without input parameters
 * @param <C> Use case callback
 */
public interface IUseCaseNoInput<C extends IUseCaseOutputCallback> extends IUseCase<C> {
    /**
     * Main method used for use case run
     */
    void start();
}