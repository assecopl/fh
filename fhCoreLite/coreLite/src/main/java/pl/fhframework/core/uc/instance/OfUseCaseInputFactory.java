package pl.fhframework.core.uc.instance;

import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by pawel.ruta on 2018-08-01.
 */
@Component(OfUseCaseInputFactory.NAME)
public class OfUseCaseInputFactory extends ConstructorUseCaseInputFactory {
    public static final String NAME = "ofUseCaseInputFactory";

    @Override
    public Object getInstance(Class aClass) {
        Method method = null;
        try {
            method = aClass.getMethod("of");
            return method.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            FhLogger.warn(String.format("%s doesn't have of factory method, reverting to ConstructorUseCaseInputFactory", aClass.getName()));
        }

        if (ClassUtils.hasConstructor(aClass)) {
            return ReflectionUtils.createClassObject(aClass);
        }

        return super.getInstance(aClass);
    }
}
