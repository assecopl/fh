package pl.fhframework.compiler.core.services.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.compiler.core.generator.BindingParser;
import pl.fhframework.compiler.core.generator.FhServicesTypeProvider;
import pl.fhframework.compiler.core.generator.MethodDescriptor;
import pl.fhframework.compiler.core.generator.ServiceMethodDescriptor;
import pl.fhframework.compiler.core.model.generator.DynamicModelClassJavaGenerator;
import pl.fhframework.compiler.core.rules.service.RulesServiceExtImpl;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.compiler.core.services.DynamicServiceMetadata;
import pl.fhframework.compiler.core.services.meta.ServiceInfo;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.aspects.snapshots.SnapshotsModelAspect;
import pl.fhframework.compiler.core.services.dynamic.model.*;
import pl.fhframework.core.FhAuthorizationException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenericExpressionConverter;
import pl.fhframework.core.generator.IExpressionConverter;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.services.MethodPointer;
import pl.fhframework.core.services.service.FhServicesServiceImpl;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.integration.IEndpointAccess;
import pl.fhframework.integration.IntegrationUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.modules.services.ServiceTypeEnum;
import pl.fhframework.validation.IValidationResults;
import pl.fhframework.validation.ValidationResults;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-12-01.
 */
@org.springframework.stereotype.Service
@Primary
public class FhServicesServiceExtImpl extends FhServicesServiceImpl implements IExpressionConverter {
    @Autowired
    private FhServicesTypeProvider servicesTypeProvider;

    @Autowired
    private GenericExpressionConverter genericExpressionConverter;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RulesServiceExtImpl rulesService;

    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Autowired
    private ServiceValidator serviceValidator;

    @Autowired(required = false)
    private IEndpointAccess endpointAccess;

    @Autowired
    @Lazy
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public static final Class[] JAXB_CONTENT_CLASSES = {Service.class};

    private static ThreadLocal<Map<String, MethodPointer>> serviceLookupCache = new ThreadLocal<>();

    private final Map<DynamicClassName, Collection<RequestMappingInfo>> registerredMappings = new ConcurrentHashMap<>();
    private final Map<String, String> registerredPaths = new ConcurrentHashMap<>();

    @Override
    public <T> T getService(String serviceName) {
        return applicationContext.getBean((Class<T>) servicesTypeProvider.getServiceClass(serviceName));
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


    public <T> T runServicePelArgs(String serviceName, String operationName, String... args) {
        return runService(serviceName, operationName, rulesService.getRuleArgs(args));
    }

    public String getEndpointUrl(Service service) {
        if (service.getServiceType() == ServiceTypeEnum.RestClient && endpointAccess != null) {
            if (!StringUtils.isNullOrEmpty(service.getEndpointName())) {
                return endpointAccess.findOneUrlByName(service.getEndpointName());
            }
            if (!StringUtils.isNullOrEmpty(service.getEndpointUrl())) {
                return service.getEndpointUrl();
            }
        }
        return null;
    }

    private <T> T runService(MethodPointer serviceOperation, Object... args) {
        try {
            return (T) serviceOperation.getObjectMethod().invoke(serviceOperation.getObject(), args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof FhAuthorizationException) {
                throw (FhAuthorizationException) e.getTargetException();
            }
            throw new FhException(String.format("Error while running service '%s.%s'", servicesTypeProvider.getServiceName(serviceOperation.getObject().getClass()), serviceOperation.getObjectMethod()), e.getTargetException());
        } catch (Exception e) {
            throw new FhException(String.format("Error while running service '%s.%s'", servicesTypeProvider.getServiceName(serviceOperation.getObject().getClass()), serviceOperation.getObjectMethod()), e);
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
                        if (args[i] != null && !ReflectionUtils.isAssignablFrom(foundParamClasses[i], args[i].getClass()) && !BindingParser.NullType.class.isAssignableFrom(args[i].getClass())) {
                            return false;
                        }
                    }
                    return true;
                }).findFirst().get();
    }

    @Override
    public String convertToShortNames(String expression) {
        return genericExpressionConverter.convertSymbolNames(expression, FhServicesTypeProvider.SERVICE_PREFIX, servicesTypeProvider::getShortOperationName);
    }

    @Override
    public String convertToFullNames(String expression) {
        return genericExpressionConverter.convertSymbolNames(expression, FhServicesTypeProvider.SERVICE_PREFIX, servicesTypeProvider::getFullOperationName);
    }

    @Override
    public Set<DynamicClassName> resolveCalledServices(String expression) {
        return searchCalledServices(expression, true).stream().map(symbol -> {
            String fullName = symbol.getName();
            return DynamicClassName.forClassName(fullName);
        }).collect(Collectors.toSet());
    }

    public List<GenericExpressionConverter.SymbolInExpression> searchCalledServices(String expression, boolean forDependecies) {
        return genericExpressionConverter.searchCalledSymbols(expression, FhServicesTypeProvider.SERVICE_PREFIX, forDependecies);
    }

    public Service readService(URL url) {
        try {
            SnapshotsModelAspect.turnOff();
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTENT_CLASSES);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final Service service = (Service) jaxbUnmarshaller.unmarshal(url);

            return service;
        } catch (JAXBException e) {
            throw new RuntimeException("Error reading XML file", e);
        } finally {
            SnapshotsModelAspect.turnOn();
        }
    }

    public Service readService(ServiceInfo serviceInfo) {
        return readService(serviceInfo.getUrl());
    }


    public void update(Service service) throws JAXBException {
        if (service.getServiceType() == null) {
            service.setServiceType(ServiceTypeEnum.DynamicService);
        }
        JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTENT_CLASSES);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final File file = service.getServiceInfo().getWritablePath().toFile();
        final File directory = file.getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        marshaller.marshal(service, file);

        DynamicClassName dcn = DynamicClassName.forClassName(service.getServiceInfo().getFullClassName());
        dynamicClassRepository.updateDynamicClass(dcn);
    }

    public Set<DynamicClassName> provideDependencies(Service service) {
        Set<DynamicClassName> dependencies = new HashSet<>();

        for (Operation operation : service.getOperations()) {
            dependencies.addAll(rulesService.provideDependencies(operation.getRule()));
        }

        return dependencies;
    }

    public void validate(Service service, IValidationResults validationResults) {
        serviceValidator.validate(service, validationResults, registerredPaths);
        DynamicClassName serviceDC = DynamicClassName.forClassName(service.getId());

        // todo: compile provided java class, not updated class in repository
        //validateByComplie(validationResults, service, serviceDC);

        if (validationResults.hasAtLeastErrors()) {
            dynamicClassRepository.setValid(serviceDC, false);
        } else {
            dynamicClassRepository.setValid(serviceDC, true);
        }
    }

    public void validate(Operation operation, Service service, IValidationResults validationResults) {
        serviceValidator.validate(operation, service, validationResults, registerredPaths);
        DynamicClassName serviceDC = DynamicClassName.forClassName(service.getId());

        // todo: compile provided java class, not updated class in repository
        //validateByComplie(validationResults, service, serviceDC);

        if (validationResults.hasAtLeastErrors()) {
            dynamicClassRepository.setValid(serviceDC, false);
        } else {
            validate(service, new ValidationResults());
        }
    }

    private void validateByComplie(IValidationResults validationResults, Service service, DynamicClassName serviceDC) {
        if (!validationResults.hasAtLeastErrors()) { // do not compile local rules
            // do not allow to run Service which doesn't compile
            try {
                dynamicClassRepository.getOrCompileDynamicClass(serviceDC);
            } catch (Exception exp) {
                FhLogger.error(exp);
                validationResults.addCustomMessage(service, "Diagram", FhLogger.getCauseMessage(exp), PresentationStyleEnum.ERROR);
            }
        }
    }

    @Override
    public void startServiceLookupCache() {
        serviceLookupCache.set(new HashMap<>());
    }

    @Override
    public void stopServiceLookupCache() {
        serviceLookupCache.set(null);
    }

    public void registerRestService(Class<?> restBean, DynamicServiceMetadata metadata) {
        try {
            if (!dynamicClassRepository.isValid(metadata.getDynamicClassName())) {
                return;
            }
            unregisterRestService(metadata);

            if (restBean == null) {
                dynamicClassRepository.getOrCompileDynamicClass(metadata.getDynamicClassName());
                return;
            }

            Object restController = applicationContext.getBean(restBean);

            RequestMapping baseRequestMapping = restBean.getAnnotation(RequestMapping.class);
            final String[] baseUri = new String[]{""};
            if (baseRequestMapping != null) {
                if (baseRequestMapping.value().length > 0) {
                    baseUri[0] = baseRequestMapping.value()[0];
                } else if (baseRequestMapping.path().length > 0) {
                    baseUri[0] = baseRequestMapping.path()[0];
                }
            }

            for (Method method : restBean.getDeclaredMethods()) {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                if (requestMapping != null) {
                    String[] paths = requestMapping.path();
                    if (paths.length == 0) {
                        paths = new String[] {""};
                    }
                    RequestMappingInfo requestMappingInfo = RequestMappingInfo
                            .paths(Arrays.stream(paths).map(path -> baseUri[0] + path).collect(Collectors.toList()).toArray(new String[]{}))
                            .methods(requestMapping.method())
                            .produces(MediaType.APPLICATION_JSON_UTF8_VALUE)
                            .build();

                    requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                    requestMappingHandlerMapping.
                            registerMapping(requestMappingInfo, restController, method);
                    Collection<RequestMappingInfo> mappings = registerredMappings.computeIfAbsent(metadata.getDynamicClassName(), dynamicClassName -> new ConcurrentLinkedQueue<>());
                    mappings.add(requestMappingInfo);

                    requestMappingInfo.getPatternsCondition().getPatterns().forEach(path -> registerredPaths.put(IntegrationUtils.cleanUri(path), metadata.getDynamicClassName().toFullClassName()));
                }
            }
            if (!StringUtils.isNullOrEmpty(metadata.getService().getBaseUri())) {
                registerredPaths.put(IntegrationUtils.cleanUri(metadata.getService().getBaseUri()), metadata.getDynamicClassName().toFullClassName());
            }
        } catch (Exception e) {
            FhLogger.error(String.format("Can't register REST service '%s'", metadata.getDynamicClassName().getBaseClassName()), e);
        }

    }

    public void unregisterRestService(DynamicServiceMetadata metadata) {
        try {
            Collection<RequestMappingInfo> mappings = registerredMappings.remove(metadata.getDynamicClassName());
            if (mappings != null) {
                mappings.forEach(requestMappingInfo -> {
                    requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                    requestMappingInfo.getPatternsCondition().getPatterns().stream().map(IntegrationUtils::cleanUri).forEach(registerredPaths::remove);
                });
            }
            if (!StringUtils.isNullOrEmpty(metadata.getService().getBaseUri())) {
                registerredPaths.remove(IntegrationUtils.cleanUri(metadata.getService().getBaseUri()));
            }

        } catch (Exception e) {
            FhLogger.error(String.format("Can't unregister REST service '%s'", metadata.getDynamicClassName().getBaseClassName()), e);
        }
    }


    public void createDefaultRest(Service service) {
        service.setServiceType(ServiceTypeEnum.RestService);
        service.setBaseUri("/" + StringUtils.firstLetterToLower(service.getLabel()));
        service.getOperations().forEach(this::createDefaultRestOperation);
    }

    private void createDefaultRestOperation(Operation operation) {
        RestProperties restProperties = new RestProperties();
        operation.setRestProperties(restProperties);
        restProperties.setResourceUri("/" + StringUtils.firstLetterToLower(operation.getRule().getLabel()));
        restProperties.setHttpMethod(RestMethodTypeEnum.GET);

        operation.getRule().getInputParams().forEach(parameterDefinition -> {
            createDefaultInputParam(restProperties, parameterDefinition);
        });
    }

    private void createDefaultInputParam(RestProperties restProperties, ParameterDefinition parameterDefinition) {
        RestParameter restParameter = new RestParameter();
        restProperties.getRestParameters().add(restParameter);

        restParameter.setRef(parameterDefinition.getName());
        RestParameterTypeEnum restParamType = isPredefinedType(parameterDefinition.getType()) ? RestParameterTypeEnum.Query : RestParameterTypeEnum.Body;
        restParameter.setType(restParamType);
        if (restParamType == RestParameterTypeEnum.Body) {
            restProperties.setHttpMethod(RestMethodTypeEnum.POST);
        }
    }

    public void clearRestProperties(Service service) {
        service.setServiceType(ServiceTypeEnum.DynamicService);
        service.setBaseUri(null);
        service.getOperations().forEach(operation -> operation.setRestProperties(null));
    }

    private  boolean isPredefinedType(String type) {
        return DynamicModelClassJavaGenerator.TYPE_MAPPER.get(type) != null;
    }

    public String getServiceForUri(String uri) {
        return registerredPaths.get(IntegrationUtils.cleanUri(uri));
    }

    public List<ServiceMethodDescriptor> getServices() {
        List<ServiceMethodDescriptor> servicesList = servicesTypeProvider.getMethods(DynamicFhServiceManager.SERVICE_HINT_TYPE).stream().filter(MethodDescriptor::isHintable).
                map(ServiceMethodDescriptor.class::cast).collect(Collectors.toList());
        servicesList.sort(Comparator.comparing(ServiceMethodDescriptor::getName));

        return servicesList;
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
