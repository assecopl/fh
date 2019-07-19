package pl.fhframework.core.uc;

import java.util.function.Consumer;

/**
 * Helper interface for serving custom action in case UseCase is canceled by external action (e.g. click in navigation tree menu).
 */
public interface IUseCaseCancelEvent {

    /**
     * Custom action for canceling usecase. If termination is allowed "callbackMethod" should be called
     *
     * @param callbackMethod - method to execute on
     */
    void onCancel(Consumer<Void> callbackMethod);

}
