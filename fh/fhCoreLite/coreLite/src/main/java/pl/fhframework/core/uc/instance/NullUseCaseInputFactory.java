package pl.fhframework.core.uc.instance;

import org.springframework.stereotype.Component;
import pl.fhframework.core.uc.IUseCaseOutputCallback;

import java.lang.reflect.InvocationHandler;

/**
 * Created by pawel.ruta on 2018-08-01.
 */
@Component(NullUseCaseInputFactory.NAME)
public class NullUseCaseInputFactory implements IUseCaseParametersFactory {
    public static final String NAME = "nullUseCaseInputFactory";

    @Override
    public Object getInstance(Class aClass) {
        if (aClass.isPrimitive()) {
            if (int.class.isAssignableFrom(aClass)) {
                return Integer.valueOf(0);
            }
            if (long.class.isAssignableFrom(aClass)) {
                return Long.valueOf(0l);
            }
            if (short.class.isAssignableFrom(aClass)) {
                return Short.valueOf((short) 0);
            }
            if (boolean.class.isAssignableFrom(aClass)) {
                return Boolean.FALSE;
            }
            if (float.class.isAssignableFrom(aClass)) {
                return Float.valueOf(0.0f);
            }
            if (double.class.isAssignableFrom(aClass)) {
                return Double.valueOf(0.0);
            }
        }
        return null;
    }

    public  IUseCaseOutputCallback createCallback(Class clazz) {
        return (IUseCaseOutputCallback) java.lang.reflect.Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                createHandler(clazz));
    }

    protected <T extends IUseCaseOutputCallback> InvocationHandler createHandler(Class<? extends T> clazz) {
        return (proxy, method, args) -> null;
    }
}
