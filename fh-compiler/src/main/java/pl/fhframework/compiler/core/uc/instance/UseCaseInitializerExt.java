package pl.fhframework.compiler.core.uc.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.core.uc.instance.UseCaseInitializer;
import pl.fhframework.core.uc.meta.UseCaseInfo;

/**
 * Created by pawel.ruta on 2018-08-03.
 */
@Component
@Primary
public class UseCaseInitializerExt extends UseCaseInitializer {
    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Override
    protected Class<?> getUseCaseClass(UseCaseInfo useCaseInfo) {
        Class<?> useCaseClass = useCaseInfo.getClazz();
        if (useCaseClass == null) {
            if (useCaseInfo.isDynamic()) {
                useCaseClass = dynamicClassRepository.getOrCompileDynamicClass(DynamicClassName.forClassName(useCaseInfo.getId()));
            }
            else {
                throw new IllegalArgumentException("No class for use case " + useCaseInfo.getId());
            }
        }

        return useCaseClass;
    }
}
