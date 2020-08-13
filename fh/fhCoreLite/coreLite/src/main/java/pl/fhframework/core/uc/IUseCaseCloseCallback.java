package pl.fhframework.core.uc;

/**
 * Represents use case callback that is invoked when child use case is closed
 */
public interface IUseCaseCloseCallback extends IUseCaseOutputCallback {
    /**
     *  Closes current child use case and invokes callback in parent use case
     */
    void close();

    /**
     * Creates use case callback from provided {@link Runnable} (close) instance
     * @param callbackMethod Instance of method implementing {@link Runnable} for use case return
     * @return Callback instance from provided parameter
     */
    static IUseCaseCloseCallback getCallback(Runnable callbackMethod) {
        return callbackMethod::run;
    }

}
