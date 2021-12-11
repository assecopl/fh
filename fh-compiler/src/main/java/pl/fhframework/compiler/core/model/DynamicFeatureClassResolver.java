package pl.fhframework.compiler.core.model;

import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.maps.features.IFeature;
import pl.fhframework.helper.AutowireHelper;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by pawel.ruta on 2019-01-16.
 */
@Service
public class DynamicFeatureClassResolver {
    private AtomicReference<DynamicClassRepository> dynamicClassRepository = new AtomicReference<>();

    Class<? extends IFeature> getDynamicFeatureClass(DynamicClassName dynamicClassName) {
        return (Class<? extends IFeature>) getDynamicClassRepository().getOrCompileDynamicClass(dynamicClassName);
    }

    private DynamicClassRepository getDynamicClassRepository() {
        if (dynamicClassRepository.get() == null) {
            dynamicClassRepository.set(AutowireHelper.getBean(DynamicClassRepository.class));
        }

        return dynamicClassRepository.get();
    }
}
