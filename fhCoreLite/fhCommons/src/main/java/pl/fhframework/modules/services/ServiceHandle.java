package pl.fhframework.modules.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Type;

/**
 * Created by pawel.ruta on 2018-10-19.
 */
@EqualsAndHashCode
@Data
@AllArgsConstructor
@Builder
public class ServiceHandle<T extends IDescribableService<U>, U> implements Type {
    private String serviceClassName;

    private Class<? extends T> serviceInterface;

    private U serviceDescriptor;

    @Override
    public String getTypeName() {
        return serviceClassName;
    }

    public String getId() {
        return serviceClassName + ":" + serviceInterface.getName();
    }
}
