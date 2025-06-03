package pl.fhframework.compiler.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.compiler.core.generator.MethodDescriptor;
import pl.fhframework.compiler.core.generator.RuleMethodDescriptor;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Permission;
import pl.fhframework.SessionManager;
import pl.fhframework.core.security.AuthorizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2018-02-15.
 */
@Component
@Getter
public class RulesListCache {
    @Autowired
    private AuthorizationManager authorizationManager;

    private int version = -1;

    private List<MethodDescriptor> cachedMethods = new ArrayList<>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public List<MethodDescriptor> getRulesList(int version, List<MethodDescriptor> methods) {
        lock.readLock().lock();

        try {
            if (this.version != version) {
                lock.readLock().unlock();
                lock.writeLock().lock();

                try {
                    cachedMethods.clear();

                    for (MethodDescriptor method : methods) {
                        RuleMethodDescriptor ruleMethodDescriptor = (RuleMethodDescriptor) method;
                        if (ruleMethodDescriptor.isRuleStatic()) {
                            if (authorizationManager.hasPermission(ruleMethodDescriptor.getMethod())) {
                                cachedMethods.add(method);
                            }
                        } else if (ruleMethodDescriptor.getMetadata().getRule().getPermissions().size() == 0) {
                            cachedMethods.add(method);
                        } else if (authorizationManager.hasAnyFunction(SessionManager.getUserSession().getSystemUser().getBusinessRoles(),
                                ruleMethodDescriptor.getMetadata().getRule().getPermissions().stream().map(Permission::getName).collect(Collectors.toList()))) {
                            cachedMethods.add(method);
                        }
                    }

                    this.version = version;
                }
                finally {
                    lock.readLock().lock();
                    lock.writeLock().unlock();
                }
            }

            return cachedMethods;
        }
        finally {
            lock.readLock().unlock();
        }
    }
}
