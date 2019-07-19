package pl.fhframework.core.uc;

public interface IUseCaseRunCallback extends IUseCaseOutputCallback {

    void run();

    static IUseCaseRunCallback getCallback(Runnable callbackMethod) {
        return callbackMethod::run;
    }
}