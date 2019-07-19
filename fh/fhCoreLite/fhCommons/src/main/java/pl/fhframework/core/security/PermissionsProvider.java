package pl.fhframework.core.security;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;

/**
 * Created by pawel.ruta on 2018-02-14.
 */
public interface PermissionsProvider {
    /**
     * Checks if current context has permission to action, use case, rule, service
     *
     * @param method - method
     * @return true if any role has access, false otherwise
     */
    boolean hasPermission(Method method);

    /**
     * Checks if given business roles(subject, probably user roles) has permission to action, use case, rule, service
     *
     * @param clazz - class
     * @return true if any role has access, false otherwise
     */
    boolean hasPermission(Class clazz);

    /**
     * Returns a collection of declared system functions for annotated element (e.g. class or method).
     *
     * @param annotatedElement annotated element, e.g. class or method.
     * @return collection of declared system functions of given annotated element.
     */
    Set<String> getDeclaredFunctions(AnnotatedElement annotatedElement);

    /**
     * Returns a collection of declared system roles for annotated element (e.g. class or method).
     *
     * @param annotatedElement annotated element, e.g. class or method.
     * @return collection of declared system roles of given annotated element.
     */
    Set<String> getDeclaredRoles(AnnotatedElement annotatedElement);

}
