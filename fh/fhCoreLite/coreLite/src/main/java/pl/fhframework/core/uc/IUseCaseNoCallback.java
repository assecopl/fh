package pl.fhframework.core.uc;

/**
 * Represents void use case callback
 */
public interface IUseCaseNoCallback extends IUseCaseOutputCallback {

    static final IUseCaseNoCallback EMPTY = new IUseCaseNoCallback() {};

    /**
     * Creates anonymous instance use case callback
     * @return Callback instance
     */
    static IUseCaseNoCallback getCallback() {
        return new IUseCaseNoCallback(){};
    }


}