package pl.fhframework.core.uc;

/**
 * Represents use case callback that is invoked when child use case is closed
 */
public interface IUseCaseCloseCallback extends IUseCaseOutputCallback {
    /**
     *  Closes current child use case and invokes callback in parent use case
     */
    void close();
}
