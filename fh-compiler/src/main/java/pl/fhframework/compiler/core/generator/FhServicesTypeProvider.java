package pl.fhframework.compiler.core.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.fhframework.compiler.cache.UserSessionCache;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassFilter;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.compiler.core.services.DynamicServiceMetadata;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.security.PermissionModificationListener;
import pl.fhframework.core.services.FhService;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by pawel.ruta on 2017-06-23.
 */
@Component
public class FhServicesTypeProvider implements ITypeProvider, PermissionModificationListener {
    public static String SERVICE_PREFIX = "SERVICE";

    @Autowired
    private
    DynamicClassRepository dynamicClassRepository;

    @Autowired
    private
    UseCaseModelUtils typeUtils;

    @Autowired(required = false)
    private UserSessionCache userSessionCache;

    private List<MethodDescriptor> cachedBeans;
    private Map<String, MethodDescriptor> cachedBeansMap;
    private Map<String, String> cachedAliasToFullNameMapping;
    private Map<String, String> cachedFullNameToAliasMapping;
    private Map<String, IClassInfo> cachedFullNameToClassInfo = new HashMap<>();

    private Map<String, List<MethodDescriptor>> cachedOperationsMap = new HashMap<>();

    private int version;

    private boolean refreshNeeded = true;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Type getSupportedType() {
        return DynamicFhServiceManager.SERVICE_HINT_TYPE;
    }

    @Override
    public List<MethodDescriptor> getMethods(Type ofType) {
        return getMethods(ofType, false);
    }

    @Override
    public List<MethodDescriptor> getMethods(Type ofType, boolean onlyPermitted) {
        lock.readLock().lock();

        try {
            List<MethodDescriptor> methods = cachedBeans;
            if (refreshNeeded) {
                methods = rebuildMethods();
            }

            Type[] argumentType = ReflectionUtils.getGenericArguments(ofType);
            if (argumentType.length == 0) {
                if (onlyPermitted) {
                    // hiding services without permission from combo
                    if (userSessionCache != null) {
                        return userSessionCache.getServicesListCache().getServicesList(version, methods);
                    }
                }

                return new ArrayList<>(methods);
            } else {
                List<MethodDescriptor> operations = getOperations(argumentType[0]);
                if (onlyPermitted) {
                    // hiding services without permission from combo
                    if (userSessionCache != null) {
                        return userSessionCache.getServicesListCache().getOperationsList(version, argumentType[0].getTypeName(), operations);
                    }
                }

                return operations;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private List<MethodDescriptor> getOperations(Type type) {
        if (!cachedOperationsMap.containsKey(type.getTypeName())) {
            rebuildOperations(type);
        }
        return cachedOperationsMap.get(type.getTypeName());
    }

    public void refresh() {
        // todo: clearing all cache is not optimal !!!
        refreshNeeded = true;
    }

    private void clear() {
        // todo: clearing all cache is not optimal !!!
        cachedBeans = null;
        cachedBeansMap = null;
        cachedAliasToFullNameMapping = null;
        cachedFullNameToAliasMapping = null;
        cachedFullNameToClassInfo.clear();
        cachedOperationsMap.clear();
    }

    @Override
    public String toTypeLiteral() {
        return null;
    }

    public Class<?> getServiceClass(String fullName) {
        lock.readLock().lock();
        try {
            if (refreshNeeded) {
                rebuildMethods();
            }
        }
        finally {
            lock.readLock().unlock();
        }

        ServiceMethodDescriptor descriptor = (ServiceMethodDescriptor) cachedBeansMap.get(fullName);
        if (descriptor.isServiceStatic()) {
            return descriptor.getReturnType();
        } else {
            return dynamicClassRepository.getOrCompileDynamicClass(descriptor.getMetadata().getDynamicClassName());
        }
    }

    public IClassInfo getClassInfo(String fullName) {
        return cachedFullNameToClassInfo.get(fullName);
    }

    private List<MethodDescriptor> rebuildMethods() {
        lock.readLock().unlock();
        lock.writeLock().lock();

        try {
            if (refreshNeeded) {
                clear();

                List<IClassInfo> servicesList = listServices();

                MultiValueMap<String, String> allServceNamesWithPackage = new LinkedMultiValueMap<>();

                servicesList.forEach(clazzName -> {
                    String serviceSimpleName = clazzName.getClassName().getBaseClassName();
                    String serviceFullName = clazzName.getClassName().toFullClassName();

                    String serviceSimpleNameLc = serviceSimpleName.toLowerCase();
                    allServceNamesWithPackage.computeIfAbsent(serviceSimpleNameLc, name -> new LinkedList<>());
                    if (!allServceNamesWithPackage.get(serviceSimpleNameLc).contains(serviceFullName)) {
                        allServceNamesWithPackage.get(serviceSimpleNameLc).add(serviceFullName);
                    }
                    cachedFullNameToClassInfo.put(serviceFullName, clazzName);
                });

                List<MethodDescriptor> newCachedBeans = new LinkedList<>();
                Map<String, MethodDescriptor> newCachedBeansMap = new LinkedHashMap<>();
                cachedAliasToFullNameMapping = new HashMap<>();
                cachedFullNameToAliasMapping = new HashMap<>();

                allServceNamesWithPackage.forEach((simpleName, fullNames) -> {
                    fullNames.forEach(fullName -> {
                        IClassInfo classInfo = cachedFullNameToClassInfo.get(fullName);

                        Class<?> beanClass;
                        Type beanGenericType;
                        String beanName;
                        if (classInfo.isDynamic()) {
                            beanGenericType = ReflectionUtils.createParametrizedType(DynamicFhServiceManager.SERVICE_HINT_TYPE, new HolderType(getFullClassName(classInfo)));
                            beanClass = ReflectionUtils.getRawClass(beanGenericType);
                            beanName = StringUtils.firstLetterToLower(classInfo.getClassName().getBaseClassName());

                        } else {
                            beanClass = getStaticClass(classInfo.getClassName());
                            beanGenericType = beanClass;
                            beanName = getServiceName(beanClass);
                        }
                        fullName = String.format("%s.%s", classInfo.getClassName().getPackageName(), beanName);
                        if (fullNames.size() > 1) {
                            beanName = fullName.replace(".", "_");
                        }
                        ServiceMethodDescriptor methodDescriptor = new ServiceMethodDescriptor(DynamicFhServiceManager.SERVICE_HINT_TYPE,
                                beanName, beanClass, beanGenericType, new Class<?>[]{},
                                !classInfo.isDynamic(), true, ModelElementType.BUSINESS_PROPERTY);
                        newCachedBeans.add(methodDescriptor);
                        newCachedBeansMap.put(beanName, methodDescriptor);

                        // add also a full class as not hinted but respected
                        ServiceMethodDescriptor fullMethodDescriptor = new ServiceMethodDescriptor(DynamicFhServiceManager.SERVICE_HINT_TYPE,
                                fullName, beanClass, beanGenericType, new Class<?>[]{},
                                !classInfo.isDynamic(), false, ModelElementType.BUSINESS_PROPERTY);
                        newCachedBeans.add(fullMethodDescriptor);
                        newCachedBeansMap.put(fullName, fullMethodDescriptor);
                        newCachedBeansMap.put(classInfo.getClassName().toFullClassName(), fullMethodDescriptor);

                        if (classInfo.isDynamic()) {
                            DynamicServiceMetadata metadata = getServiceMetadata(classInfo.getClassName().toFullClassName());
                            methodDescriptor.setMetadata(metadata);
                            methodDescriptor.setComment(metadata.getService().getDescription());
                            fullMethodDescriptor.setMetadata(metadata);
                            fullMethodDescriptor.setComment(methodDescriptor.getComment());
                            methodDescriptor.getCategories().addAll(metadata.getService().getCategories());
                            fullMethodDescriptor.getCategories().addAll(metadata.getService().getCategories());
                        }
                        else {
                            FhService annotation = beanClass.getAnnotation(FhService.class);
                            if (annotation != null) {
                                methodDescriptor.setComment(annotation.description());
                                fullMethodDescriptor.setComment(annotation.description());
                                methodDescriptor.getCategories().addAll(Arrays.asList(annotation.categories()));
                                fullMethodDescriptor.getCategories().addAll(Arrays.asList(annotation.categories()));
                            }
                        }

                        cachedAliasToFullNameMapping.put(beanName, classInfo.getClassName().toFullClassName());
                        cachedFullNameToAliasMapping.put(fullName, beanName);
                        cachedFullNameToAliasMapping.put(classInfo.getClassName().toFullClassName(), beanName);
                    });
                });
                cachedBeansMap = newCachedBeansMap;
                cachedBeans = newCachedBeans;

                version++;

                refreshNeeded = false;
            }
            return cachedBeans;
        } finally {
            lock.readLock().lock();
            lock.writeLock().unlock();
        }
    }

    protected String getFullClassName(IClassInfo classInfo) {
        return classInfo.getClassName().toFullClassName();
    }

    private void rebuildOperations(Type type) {
        lock.readLock().unlock();
        lock.writeLock().lock();

        try {
            if (!cachedOperationsMap.containsKey(type.getTypeName())) {
                DynamicServiceMetadata metadata = getServiceMetadata(DynamicClassName.forClassName(type.getTypeName()).toFullClassName());

                List<MethodDescriptor> operations = new ArrayList<>();

                metadata.getService().getOperations().forEach(operation -> {
                    Rule rule = operation.getRule();
                    DynamicClassName operationId = DynamicClassName.forClassName(rule.getId());
                    String name = StringUtils.firstLetterToLower(operationId.getBaseClassName());

                    DynamicRuleMetadata ruleMetadata = new DynamicRuleMetadata();
                    ruleMetadata.setRule(rule);

                    OperationMethodDescription operationDescriptor = new OperationMethodDescription(
                            DynamicFhServiceManager.SERVICE_HINT_TYPE,
                            name,
                            name,
                            false,
                            false,
                            true,
                            operationId,
                            getModelUtils(),
                            null,
                            ruleMetadata,
                            RuleType.BusinessRule,
                            operation);
                    operationDescriptor.setComment(rule.getDescription());
                    operations.add(operationDescriptor);
                });

                cachedOperationsMap.put(type.getTypeName(), operations);
            }
        } finally {
            lock.readLock().lock();
            lock.writeLock().unlock();
        }
    }

    protected DynamicServiceMetadata getServiceMetadata(String typeName) {
        return dynamicClassRepository.getMetadata(DynamicClassName.forClassName(typeName));
    }

    public String getServiceName(Class<?> clazz) {
        FhService fhService = ReflectionUtils.giveClassAnnotations(clazz, FhService.class);
        String name;
        if (!StringUtils.isNullOrEmpty(fhService.groupName())) {
            name = fhService.groupName();
        } else if (!StringUtils.isNullOrEmpty(fhService.value())) {
            name = fhService.value();
        } else {
            name = StringUtils.firstLetterToLower(clazz.getSimpleName());
        }

        return StringUtils.firstLetterToLower(name);
    }

    public String getFullServiceName(String name) {
        lock.readLock().lock();

        try {
            checkInit();
            return cachedAliasToFullNameMapping.get(name);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getShortServiceName(String fullName) {
        lock.readLock().lock();

        try {
            checkInit();
            return cachedFullNameToAliasMapping.get(fullName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getFullOperationName(String name) {
        lock.readLock().lock();

        try {
            checkInit();
            return getName(name, cachedAliasToFullNameMapping);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getShortOperationName(String fullName) {
        lock.readLock().lock();

        try {
            checkInit();
            return getName(fullName, cachedFullNameToAliasMapping);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void checkInit() {
        if (refreshNeeded) {
            rebuildMethods();
        }
    }

    private String getName(String nameToConvert, Map<String, String> mapper) {
        int lastDotIdx = nameToConvert.lastIndexOf('.');
        String serviceGroupName = mapper.get(nameToConvert.substring(0, lastDotIdx));
        return serviceGroupName + nameToConvert.substring(lastDotIdx);
    }

    @Override
    public Type resolvePartsType(Type ofType, String parts) {
        lock.readLock().lock();

        if (refreshNeeded) {
            rebuildMethods();
        }

        try {
            if (!StringUtils.isNullOrEmpty(parts) && cachedBeansMap.containsKey(parts)) {
                return cachedBeansMap.get(parts).getGenericReturnType();
            }
            return ITypeProvider.super.resolvePartsType(ofType, parts);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public MethodDescriptor getResolvedMethod(Type ofType, String parts) {
        lock.readLock().lock();

        if (refreshNeeded) {
            rebuildMethods();
        }
        try {
            if (!StringUtils.isNullOrEmpty(parts) && cachedBeansMap.containsKey(parts)) {
                return cachedBeansMap.get(parts);
            }
            return ITypeProvider.super.getResolvedMethod(ofType, parts);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void onRoleChange(String roleName) {
        lock.writeLock().lock();

        try {
            version++;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    protected List<IClassInfo> listServices() {
        return dynamicClassRepository.listClasses(DynamicClassFilter.ALL_CLASSES, DynamicClassArea.SERVICE);
    }

    protected Class getStaticClass(DynamicClassName className) {
        return dynamicClassRepository.getStaticClass(className);
    }

    protected UseCaseModelUtils getModelUtils() {
        return typeUtils;
    }

    public ServiceMethodDescriptor getMethodDescription(String fullSeviceName) {
        lock.readLock().lock();

        try {
            checkInit();
            return (ServiceMethodDescriptor) cachedBeansMap.get(cachedFullNameToAliasMapping.get(fullSeviceName));
        } finally {
            lock.readLock().unlock();
        }
    }
}
