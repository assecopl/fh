package pl.fhframework.aop.services;

import java.lang.reflect.Method;

/**
 * Created by pawel.ruta on 2018-02-12.
 */
public interface IFhSessionService {
    boolean onServiceStart(Method operation, Object owner, Object[] params);

    void onServiceEnd(Method operation, Object owner, Object[] retValHolder, boolean endSession);

    void onError(Method method, Object owner, Exception e, boolean endSession);
}
