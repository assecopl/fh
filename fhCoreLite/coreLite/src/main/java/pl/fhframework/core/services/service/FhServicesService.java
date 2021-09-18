package pl.fhframework.core.services.service;

import pl.fhframework.core.dynamic.DynamicClassName;

import java.util.Set;

/**
 * Created by pawel.ruta on 2017-12-01.
 */
public interface FhServicesService {
    <T> T getService(String serviceName);

    <T> T runService(String serviceName, String operationName, Object... args);

    <T> T runService(Class<?> serviceClass, String operationName, Object... args);

    Set<DynamicClassName> resolveCalledServices(String expression);

    void startServiceLookupCache();

    void stopServiceLookupCache();
}
