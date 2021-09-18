package pl.fhframework.modules.services;

import java.util.List;

/**
 * Created by pawel.ruta on 2018-10-19.
 */
public interface IServiceLocator {
    <T extends IDescribableService<U>, U> U getDescriptor(Class<? extends T> serviceClass);

    <T extends IDescribableService<U>, U> List<ServiceHandle<T, U>> getServices(Class<? extends T> serviceInterface);
}
