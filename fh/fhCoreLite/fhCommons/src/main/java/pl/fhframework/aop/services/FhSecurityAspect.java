package pl.fhframework.aop.services;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.core.FhAuthorizationException;
import pl.fhframework.core.security.PermissionsProvider;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Paweł Ruta
 */
@Aspect
@Component
public class FhSecurityAspect {
    @Autowired(required = false)
    private PermissionsProvider permissionProvder;

    @Around("within(@pl.fhframework.core.rules.BusinessRule *) && (within(@pl.fhframework.core.security.annotations.SystemFunction *) or within(@pl.fhframework.core.security.annotations.SystemFunctions *) or within(@pl.fhframework.core.security.annotations.SystemRole *) or within(@pl.fhframework.core.security.annotations.SystemRoles *)) && execution(public * *(..)) && not @annotation(pl.fhframework.core.security.annotations.SystemFunction) && not @annotation(pl.fhframework.core.security.annotations.SystemFunctions) && not @annotation(pl.fhframework.core.security.annotations.SystemRole) && not @annotation(pl.fhframework.core.security.annotations.SystemRoles)")
    public Object ruleClassSecurityPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        checkPermission(joinPoint.getTarget().getClass(), "Rule");

        return joinPoint.proceed();
    }

    @Around("within(@pl.fhframework.core.rules.BusinessRule *) && (@annotation(pl.fhframework.core.security.annotations.SystemFunction) or @annotation(pl.fhframework.core.security.annotations.SystemFunctions) or @annotation(pl.fhframework.core.security.annotations.SystemRole) or @annotation(pl.fhframework.core.security.annotations.SystemRoles))")
    public Object ruleMethodSecurityPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        checkPermission(((MethodSignature)joinPoint.getSignature()).getMethod(), "Rule");

        return joinPoint.proceed();
    }

    @Around("within(@pl.fhframework.core.services.FhService *) && (within(@pl.fhframework.core.security.annotations.SystemFunction *) or within(@pl.fhframework.core.security.annotations.SystemFunctions *) or within(@pl.fhframework.core.security.annotations.SystemRole *) or within(@pl.fhframework.core.security.annotations.SystemRoles *)) && execution(public * *(..)) && not @annotation(pl.fhframework.core.security.annotations.SystemFunction) && not @annotation(pl.fhframework.core.security.annotations.SystemFunctions) && not @annotation(pl.fhframework.core.security.annotations.SystemRole) && not @annotation(pl.fhframework.core.security.annotations.SystemRoles)")
    public Object serviceClassSecurityPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        checkPermission(joinPoint.getTarget().getClass(), "Service");

        return joinPoint.proceed();
    }

    @Around("within(@pl.fhframework.core.services.FhService *) && (@annotation(pl.fhframework.core.security.annotations.SystemFunction) or @annotation(pl.fhframework.core.security.annotations.SystemFunctions) or @annotation(pl.fhframework.core.security.annotations.SystemRole) or @annotation(pl.fhframework.core.security.annotations.SystemRoles))")
    public Object serviceMethodSecurityPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        checkPermission(((MethodSignature)joinPoint.getSignature()).getMethod(), "Service");

        return joinPoint.proceed();
    }

    private void checkPermission(Class<?> aClass, String type) {
        if (permissionProvder == null || !permissionProvder.hasPermission(aClass)) {
            throw new FhAuthorizationException(
                    String.format("User has no permission to execute %s '%s'. Required one of permissions: %s",
                            type, aClass.getSimpleName(), buildPermissionMessage(aClass)));
        }
    }

    private void checkPermission(Method method, String type) {
        if (permissionProvder == null || !permissionProvder.hasPermission(method)) {
            throw new FhAuthorizationException(
                    String.format("User has no permission to execute %s '%s.%s'. Required one of permissions: %s",
                            type, method.getDeclaringClass().getSimpleName(), method.getName(), buildPermissionMessage(method)));
        }
    }

    private String buildPermissionMessage(AnnotatedElement annotatedElement) {
        StringBuilder builder = new StringBuilder();
        buildPermissionMessage(builder, permissionProvder.getDeclaredFunctions(annotatedElement));
        buildPermissionMessage(builder, permissionProvder.getDeclaredRoles(annotatedElement));
        return builder.toString();
    }

    private void buildPermissionMessage(StringBuilder builder, Set<String> permissions) {
        permissions.forEach(
                permission -> {
                    if (builder.length() > 0) {
                        builder.append(',').append(' ');
                    }
                    builder.append(permission);
                }
        );
    }

}
