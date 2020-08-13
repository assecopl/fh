package pl.fhframework.core.services;

import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Created by pawel.ruta on 2018-04-06.
 */
@Getter
public class MethodPointer {
    private Object object;
    private Method objectMethod;

    public static MethodPointer of(Object object, Method objectMethod) {
        MethodPointer ruleReference = new MethodPointer();
        ruleReference.object = object;
        ruleReference.objectMethod = objectMethod;

        return ruleReference;
    }
}
