package pl.fhframework.integration.core.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.IDescribableService;
import pl.fhframework.modules.services.ServiceHandle;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pawel.ruta on 2018-10-17.
 */
@Component
public class RestClientFactory implements IRestClientFactory {
    @Autowired
    private RestClientProxy restClientProxy;

    @Autowired
    private ApplicationContext context;

    private Map<String, Object> servicesCache = new HashMap<>();

    public <T> T getClient(Class<T> clazz, String endpointOrUrl) {
        return getClient(null, clazz, endpointOrUrl);
    }

    public <T extends IDescribableService<?>> T getClient(ServiceHandle<? extends T, ?> serviceHandle) {
        // local service (no endpoint or url)
        return (T) context.getBean(ReflectionUtils.getClassForName(serviceHandle.getTypeName()));
    }

    protected <T> T getClient(ServiceHandle<? extends T, ?> serviceHandle, Class<? extends T> clazz, String endpointOrUrl) {
        return (T) servicesCache.computeIfAbsent(getInterfaceId(clazz, endpointOrUrl),
                key -> Proxy.newProxyInstance(FhCL.classLoader, new Class[]{clazz},
                        (proxy, method, args) -> restClientProxy.call(getRestDescriptor(serviceHandle, clazz, method, args, endpointOrUrl))));
    }

    protected <T> RestDescriptor getRestDescriptor(ServiceHandle<? extends T, ?> serviceHandle, Class<? extends T> clazz, Method method, Object[] args, String endpointOrUrl) {
        return fillRestDescriptor(RestDescriptor.builder(), serviceHandle, clazz, method, args, endpointOrUrl).build();
    }

    protected <T> RestDescriptor.RestDescriptorBuilder fillRestDescriptor(RestDescriptor.RestDescriptorBuilder builder,
            ServiceHandle<? extends T, ?> serviceHandle, Class<? extends T> clazz, Method method, Object[] args, String endpointOrUrl) {
        // todo: extend
        builder.endpointOrUrl(endpointOrUrl).resultType(method.getGenericReturnType());

        RequestMapping classRM = clazz.getAnnotation(RequestMapping.class);
        StringBuilder pathUri = new StringBuilder();
        if (classRM.path().length > 0) {
            pathUri.append(classRM.path()[0]);
            if (pathUri.charAt(pathUri.length() - 1) != '/') {
                pathUri.append("/");
            }
            String methodPath = getMethodPath(method);
            if (methodPath.startsWith("/")) {
                pathUri.append(methodPath.substring(1));
            }
            else {
                pathUri.append(methodPath);
            }
        }
        builder.uri(pathUri.toString()).httpMethod(getHttpMethod(method));

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            fillParameter(builder, parameters[i], args[i]);
        }
        return builder;
    }

    protected String getInterfaceId(Class<?> clazz, String endpointOrUrl) {
        return endpointOrUrl + ":" + clazz.getName();
    }

    private String getMethodPath(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null && requestMapping.path().length > 0) {
            return requestMapping.path()[0];
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null && getMapping.path().length > 0) {
            return getMapping.path()[0];
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null && postMapping.path().length > 0) {
            return postMapping.path()[0];
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null && putMapping.path().length > 0) {
            return putMapping.path()[0];
        }
        return "";
    }

    private HttpMethod getHttpMethod(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null && requestMapping.method().length > 0) {
            return HttpMethod.valueOf(requestMapping.method()[0].name());
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            return HttpMethod.GET;
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            return HttpMethod.POST;
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            return HttpMethod.PUT;
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            return HttpMethod.DELETE;
        }
        throw new IllegalArgumentException("Unknown http method");
    }

    private void fillParameter(RestDescriptor.RestDescriptorBuilder builder, Parameter parameter, Object value) {
        if (parameter.isAnnotationPresent(PathVariable.class)) {
            PathVariable annotation = parameter.getAnnotation(PathVariable.class);
            builder.uriParam(StringUtils.nvl(annotation.name(), annotation.value()), value);
        } else if (parameter.isAnnotationPresent(RequestParam.class)) {
            RequestParam annotation = parameter.getAnnotation(RequestParam.class);
            builder.queryParam(StringUtils.nvl(annotation.name(), annotation.value()), value);
        } else if (parameter.isAnnotationPresent(RequestHeader.class)) {
            RequestHeader annotation = parameter.getAnnotation(RequestHeader.class);
            builder.headerParam(StringUtils.nvl(annotation.name(), annotation.value()), value);
        } else if (parameter.isAnnotationPresent(RequestBody.class)) {
            builder.body(value);
        } else {
            throw new IllegalArgumentException("Unknown param annotations");
        }
    }
}
