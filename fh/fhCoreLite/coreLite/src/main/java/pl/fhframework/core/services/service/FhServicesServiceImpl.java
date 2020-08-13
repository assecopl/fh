package pl.fhframework.core.services.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pl.fhframework.core.FhAuthorizationException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenericExpressionConverter;
import pl.fhframework.core.rules.service.RulesServiceImpl;
import pl.fhframework.core.services.MethodPointer;
import pl.fhframework.integration.IEndpointAccess;
import pl.fhframework.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pawel.ruta on 2017-12-01.
 */
@org.springframework.stereotype.Service
public class FhServicesServiceImpl implements FhServicesService {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RulesServiceImpl rulesService;

    @Autowired(required = false)
    private IEndpointAccess endpointAccess;

    @Autowired
    @Lazy
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private static ThreadLocal<Map<String, MethodPointer>> serviceLookupCache = new ThreadLocal<>();

    private final Map<DynamicClassName, Collection<RequestMappingInfo>> registerredMappings = new ConcurrentHashMap<>();
    private final Map<String, String> registerredPaths = new ConcurrentHashMap<>();

    @Override
    public <T> T getService(String serviceName) {
        Class<T> serviceClass = (Class<T>) ReflectionUtils.tryGetClassForName(serviceName);
        if (serviceClass == null) {
            serviceClass = (Class<T>) ReflectionUtils.getClassForName(serviceName);
        }
        return applicationContext.getBean(serviceClass);
    }

    @Override
    public <T> T runService(String serviceName, String operationName, Object... args) {
        MethodPointer serviceOperation = getCachedOperation(serviceName, operationName, args);

        if (serviceOperation == null) {
            Object service = getService(serviceName);
            serviceOperation = MethodPointer.of(service, getOperationMethod(service, operationName, args));
            putInServiceCache(serviceName, operationName, args, serviceOperation);
        }

        return runService(serviceOperation, args);
    }

    @Override
    public <T> T runService(Class<?> serviceClass, String operationName, Object... args) {
        String serviceName = ReflectionUtils.getClassName(serviceClass);
        MethodPointer serviceOperation = getCachedOperation(serviceName, operationName, args);

        if (serviceOperation == null) {
            Object service = applicationContext.getBean(serviceClass);
            serviceOperation = MethodPointer.of(service, getOperationMethod(service, operationName, args));
            putInServiceCache(serviceName, operationName, args, serviceOperation);
        }

        return runService(serviceOperation, args);
    }

    @Override
    public Set<DynamicClassName> resolveCalledServices(String expression) {
        return Collections.emptySet();
    }

    private <T> T runService(MethodPointer serviceOperation, Object... args) {
        try {
            return (T) serviceOperation.getObjectMethod().invoke(serviceOperation.getObject(), args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof FhAuthorizationException) {
                throw (FhAuthorizationException) e.getTargetException();
            }
            throw new FhException(String.format("Error while running service '%s.%s'", serviceOperation.getObject().getClass().getName(), serviceOperation.getObjectMethod()), e.getTargetException());
        } catch (Exception e) {
            throw new FhException(String.format("Error while running service '%s.%s'", serviceOperation.getObject().getClass().getName(), serviceOperation.getObjectMethod()), e);
        }
    }

    private Method getOperationMethod(Object service, String operationName, Object[] args) {
        return Arrays.stream(service.getClass().getMethods()).
                filter(method -> !Modifier.isStatic(method.getModifiers()) &&
                        Modifier.isPublic(method.getModifiers()) &&
                        !method.isBridge() &&
                        method.getName().equals(operationName)).
                filter(method -> {
                    Class<?>[] foundParamClasses = method.getParameterTypes();
                    if (foundParamClasses.length != args.length) {
                        return false;
                    }
                    // each argument's class matches
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] != null && !ReflectionUtils.isAssignablFrom(foundParamClasses[i], args[i].getClass()) && args[i].getClass() != null) {
                            return false;
                        }
                    }
                    return true;
                }).findFirst().get();
    }

    @Override
    public void startServiceLookupCache() {
        serviceLookupCache.set(new HashMap<>());
    }

    @Override
    public void stopServiceLookupCache() {
        serviceLookupCache.set(null);
    }

    private MethodPointer getCachedOperation(String serviceName, String operationName, Object[] args) {
        Map<String, MethodPointer> serviceCache = serviceLookupCache.get();
        if (serviceCache != null) {
            return serviceCache.get(getOperationCacheId(serviceName, operationName, args));
        }

        return null;
    }

    private void putInServiceCache(String serviceName, String operationName, Object[] args, MethodPointer serviceOperation) {
        Map<String, MethodPointer> ruleCache = serviceLookupCache.get();
        if (ruleCache != null) {
            ruleCache.put(getOperationCacheId(serviceName, operationName, args), serviceOperation);
        }
    }

    private String getOperationCacheId(String serviceName, String operationName, Object[] args) {
        return serviceName + "." + operationName + "_&_" + args.length; // no overloading by type (arguments count must differ)
    }
}
