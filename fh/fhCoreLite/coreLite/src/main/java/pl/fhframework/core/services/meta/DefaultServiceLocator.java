package pl.fhframework.core.services.meta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.core.util.JacksonUtils;
import pl.fhframework.modules.services.ServiceLocator;
import pl.fhframework.modules.services.IDescribableService;
import pl.fhframework.modules.services.IServiceLocator;
import pl.fhframework.modules.services.ServiceHandle;
import pl.fhframework.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2018-10-19.
 */
@Component
public class DefaultServiceLocator extends ServiceLocator implements IServiceLocator {

    private Map<String, ServiceHandle> serviceDescriptorMap = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {
        INSTANCE = this;
    }

    @Override
    public <T extends IDescribableService<U>, U> U getDescriptor(Class<? extends T> serviceClass) {
        return ((ServiceHandle<T,U>) serviceDescriptorMap.get(serviceClass.getName())).getServiceDescriptor();
    }

    @Override
    public <T extends IDescribableService<U>, U> List<ServiceHandle<T, U>> getServices(Class<? extends T> serviceInterface) {
        return serviceDescriptorMap.values().stream().
                filter(serviceClass -> serviceInterface.isAssignableFrom(serviceClass.getServiceInterface())).
                map(serviceClass -> (ServiceHandle<T, U>) serviceClass).collect(Collectors.toList());
    }

    public void addService(ServiceHandle serviceHandle) {
        serviceDescriptorMap.put(serviceHandle.getTypeName(), serviceHandle);
    }

    public void removeService(String serviceClassTypeName) {
        serviceDescriptorMap.remove(serviceClassTypeName);
    }

}
