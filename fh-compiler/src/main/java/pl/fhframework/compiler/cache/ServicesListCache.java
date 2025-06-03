package pl.fhframework.compiler.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.compiler.core.generator.MethodDescriptor;
import pl.fhframework.compiler.core.generator.RuleMethodDescriptor;
import pl.fhframework.compiler.core.generator.ServiceMethodDescriptor;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Permission;
import pl.fhframework.SessionManager;
import pl.fhframework.core.security.AuthorizationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2018-02-15.
 */
@Component
@Getter
public class ServicesListCache {
    @Autowired
    private AuthorizationManager authorizationManager;

    private int version = -1;

    private List<MethodDescriptor> cachedServices = new ArrayList<>();
    private Map<String, List<MethodDescriptor>> cachedOperations = new HashMap<>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public List<MethodDescriptor> getServicesList(int version, List<MethodDescriptor> services) {
        lock.readLock().lock();

        try {
            if (this.version != version) {
                lock.readLock().unlock();
                lock.writeLock().lock();

                try {
                    cachedServices.clear();
                    cachedOperations.clear();

                    for (MethodDescriptor method : services) {
                        ServiceMethodDescriptor servicesDesc = (ServiceMethodDescriptor) method;
                        if (servicesDesc.isServiceStatic()) {
                            if (authorizationManager.hasPermission(servicesDesc.getReturnType())) {
                                cachedServices.add(method);
                            }
                        } else if (servicesDesc.getMetadata().getService().getPermissions().size() == 0 ||
                                authorizationManager.hasAnyFunction(SessionManager.getUserSession().getSystemUser().getBusinessRoles(),
                                        servicesDesc.getMetadata().getService().getPermissions().stream().map(Permission::getName).collect(Collectors.toList()))) {
                            cachedServices.add(method);
                        }
                    }

                    this.version = version;
                } finally {
                    lock.readLock().lock();
                    lock.writeLock().unlock();
                }
            }

            return cachedServices;
        } finally {
            lock.readLock().unlock();
        }
    }

    public synchronized List<MethodDescriptor> getOperationsList(int version, String typeName, List<MethodDescriptor> operations) {
        try {
            lock.readLock().lock();

            if (this.version != version) {
                try {
                    lock.readLock().unlock();
                    lock.writeLock().lock();

                    cachedServices.clear();
                    cachedOperations.clear();

                    this.version = version;
                } finally {
                    lock.readLock().lock();
                    lock.writeLock().unlock();
                }
            }

            if (!cachedOperations.containsKey(typeName)) {
                try {
                    lock.readLock().unlock();
                    lock.writeLock().lock();

                    List<MethodDescriptor> allowedOperations = new ArrayList<>();
                    for (MethodDescriptor operationMethod : operations) {
                        RuleMethodDescriptor operationRule = (RuleMethodDescriptor) operationMethod;
                        if (operationRule.getMetadata().getRule().getPermissions().size() == 0) {
                            allowedOperations.add(operationMethod);
                        } else if (authorizationManager.hasAnyFunction(SessionManager.getUserSession().getSystemUser().getBusinessRoles(),
                                operationRule.getMetadata().getRule().getPermissions().stream().map(Permission::getName).collect(Collectors.toList()))) {
                            allowedOperations.add(operationMethod);
                        }
                    }
                    cachedOperations.put(typeName, allowedOperations);
                } finally {
                    lock.readLock().lock();
                    lock.writeLock().unlock();
                }
            }

            return cachedOperations.get(typeName);
        } finally {
            lock.readLock().unlock();
        }
    }
}
