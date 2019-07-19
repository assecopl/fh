package pl.fhframework.core.uc.instance;

import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.ReflectionUtils;

/**
 * Created by pawel.ruta on 2018-08-01.
 */
@Component(ConstructorUseCaseInputFactory.NAME)
public class ConstructorUseCaseInputFactory extends NullUseCaseInputFactory {
    public static final String NAME = "constructorUseCaseInputFactory";

    @Override
    public Object getInstance(Class aClass) {
        if (ClassUtils.hasConstructor(aClass)) {
            return ReflectionUtils.createClassObject(aClass);
        }
        FhLogger.warn(String.format("%s doesn't have default constructor, reverting to NullUseCaseInputFactory", aClass.getName()));
        return super.getInstance(aClass);
    }
}
