package pl.fhframework.compiler.core.services;

import lombok.Data;
import pl.fhframework.compiler.core.dynamic.DynamicClassMetadata;
import pl.fhframework.compiler.core.services.dynamic.model.Service;

@Data
public class DynamicServiceMetadata extends DynamicClassMetadata {
    private Service service;
}
