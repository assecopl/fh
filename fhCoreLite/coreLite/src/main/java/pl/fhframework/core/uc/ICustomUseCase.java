package pl.fhframework.core.uc;

/**
 * Created by pawel.ruta on 2018-07-27.
 */
public interface ICustomUseCase extends IUseCase<IUseCaseOutputCallback> {
    default <O extends IUseCaseOutputCallback> O exit(Class<? extends O> clazz) {
        return (O) getUserSession().getUseCaseContainer().getCallback(this, clazz);
    }

    default <O extends IUseCaseOutputCallback> void registerCallback(Class<? extends O> clazz, O callback) {
        getUserSession().getUseCaseContainer().registerCallback(this, clazz, callback);
    }

    default void registerInputs(Object... inputs) {
        getUserSession().getUseCaseContainer().registerInputs(this, inputs);
    }

    void start();
}
