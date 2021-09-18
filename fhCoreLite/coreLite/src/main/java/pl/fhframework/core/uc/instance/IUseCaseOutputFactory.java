package pl.fhframework.core.uc.instance;

import pl.fhframework.core.uc.IUseCaseOutputCallback;

/**
 * Created by pawel.ruta on 2018-08-01.
 */
public interface IUseCaseOutputFactory<T extends IUseCaseOutputCallback> {
    T createCallback(Class<? extends T> clazz);
}
