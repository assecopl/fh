package pl.fhframework.core.uc.instance;

import pl.fhframework.core.uc.IUseCaseOutputCallback;

/**
 * Created by pawel.ruta on 2018-08-01.
 */
public interface IUseCaseInputFactory<T> {
    T getInstance(Class<? extends T> aClass);
}
