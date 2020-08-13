package pl.fhframework.core.uc.meta;

import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.subsystems.Subsystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UseCaseMetadataRegistry {

    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final Map<String, UseCaseInfo> USE_CASE_METADATA_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, UseCaseInfo> USE_CASE_URL_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> USE_CASE_INTERFACES_MAP_CACHE = new ConcurrentHashMap<>();
    public static final UseCaseMetadataRegistry INSTANCE = new UseCaseMetadataRegistry();
    private IUseCaseMetadataReader useCaseMetadataReader;

    private UseCaseMetadataRegistry() {
        useCaseMetadataReader = new UseCaseMetadataReader();
    }

    public Optional<UseCaseInfo> get(String useCaseQualifiedClassName) {
        LOCK.readLock().lock();
        try {
            // maps interfaces to implementations
            useCaseQualifiedClassName = optionallyResolveInterface(useCaseQualifiedClassName);

            return Optional.ofNullable(USE_CASE_METADATA_CACHE.get(useCaseQualifiedClassName));
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public Optional<UseCaseInfo> getByUrlAlias(String urlUseCaseAlias) {
        return Optional.ofNullable(USE_CASE_URL_CACHE.get(urlUseCaseAlias));
    }

    public void add(Class<? extends IUseCase> clazz, Subsystem subsystem) {
        LOCK.writeLock().lock();
        try {
            String useCaseQualifiedClassName = clazz.getName();
            UseCaseInfo useCaseMetadata = useCaseMetadataReader.buildUseCaseMetadata(clazz, subsystem);
            USE_CASE_METADATA_CACHE.put(useCaseQualifiedClassName, useCaseMetadata);
            putInterfaceMapping(useCaseMetadata);
            if (useCaseMetadata.getUrlAlias() != null) {
                USE_CASE_URL_CACHE.put(useCaseMetadata.getUrlAlias(), useCaseMetadata);
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public void addUrlAlias(UseCaseInfo useCaseInfo) {
        if (useCaseInfo.getUrlAlias() != null) {
            LOCK.writeLock().lock();
            try {
                USE_CASE_URL_CACHE.put(useCaseInfo.getUrlAlias(), useCaseInfo);
            } finally {
                LOCK.writeLock().unlock();
            }
        }
    }

    public void removeUrlAlias(String url, String ucId) {
        LOCK.writeLock().lock();
        try {
            UseCaseInfo useCaseInfo = USE_CASE_URL_CACHE.remove(url);
            if (useCaseInfo != null && !Objects.equals(useCaseInfo.getId(), ucId)) {
                USE_CASE_URL_CACHE.put(url, useCaseInfo);
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }


    public List<UseCaseInfo> getAll() {
        LOCK.readLock().lock();
        try {
            return new LinkedList<>(USE_CASE_METADATA_CACHE.values());
        } finally {
            LOCK.readLock().unlock();
        }
    }

    private String optionallyResolveInterface(String classOrInterfaceName) {
        Class clazz = ReflectionUtils.tryGetClassForName(classOrInterfaceName);
        if (clazz != null && USE_CASE_INTERFACES_MAP_CACHE.containsKey(classOrInterfaceName)) {
            Set<String> implementations = USE_CASE_INTERFACES_MAP_CACHE.get(classOrInterfaceName);
            if (implementations.size() != 1) {
                throw new FhUseCaseException("In order to run use case by " + classOrInterfaceName
                        + " interface, there should be exactly one implementation. Found: " + implementations);
            }
            return implementations.iterator().next();
        } else {
            return classOrInterfaceName;
        }
    }

    private void putInterfaceMapping(UseCaseInfo info) {
        for (Class<? extends IUseCase> interfaceClass : info.getImplementedInterfaces()) {
            Set<String> classes = USE_CASE_INTERFACES_MAP_CACHE.get(interfaceClass.getName());
            if (classes == null) {
                classes = new HashSet<>();
                USE_CASE_INTERFACES_MAP_CACHE.put(interfaceClass.getName(), classes);
            }
            classes.add(info.getClazz().getName());
        }
    }
}