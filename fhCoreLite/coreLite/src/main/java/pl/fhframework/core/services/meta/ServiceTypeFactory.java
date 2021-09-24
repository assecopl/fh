package pl.fhframework.core.services.meta;

import pl.fhframework.modules.services.ServiceTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pawel.ruta on 2018-04-09.
 */
public class ServiceTypeFactory {
    private static Map<ServiceTypeEnum, ServiceType> generators = new HashMap<>();

    public static void registerServiceType(ServiceType serviceType) {
        generators.put(serviceType.getTypeId(), serviceType);
    }

    public static ServiceType getServiceType(ServiceTypeEnum serviceTypeId) {
        return generators.get(serviceTypeId);
    }

    public static List<ServiceType> getAllServicesType() {
        return new ArrayList<>(generators.values());
    }
}
