package pl.fhframework.integration.core.client;

import pl.fhframework.modules.services.IDescribableService;
import pl.fhframework.modules.services.ServiceHandle;

/**
 * Created by pawel.ruta on 2018-10-19.
 */
public interface IRestClientFactory {
    <T> T getClient(Class<T> clazz, String endpointOrUrl);

    <T extends IDescribableService<?>> T getClient(ServiceHandle<? extends T, ?> serviceHandle);
}
