package pl.fhframework.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.rules.meta.RuleMetadataRegistry;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.security.annotations.SystemRole;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.services.FhService;
import pl.fhframework.core.services.meta.ServiceMetadataRegistry;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.SessionManager;
import pl.fhframework.model.security.SystemUser;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import javax.annotation.PostConstruct;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 *
 * This implementation requires bean of {@link IBusinessRoleLoader}. If bean is not provided all
 * methods will respond as if user has access to all system functions.
 *
 * On container startup, manager checks use cases and actions for {@code SystemFunction}. If found,
 * then it will be cached for later permission checks.
 *
 * DONE: Cache evaluated permissions for better performance.
 * DONE: Also provide "refresh" permission function. (clear permissions)
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConditionalOnProperty(value = "fhframework.security.manager.default")
public class CoreAuthorizationManager implements AuthorizationManager {

    /** All system functions */
    static final Set<Function> ALL_FUNCTIONS_CACHE = new ConcurrentSkipListSet<>();

    @Autowired
    ApplicationPrivilegesCache applicationPrivilegesCache;
    @Autowired
    ApplicationPermissionsCache permissionsCache;
    @Autowired(required = false)
    IBusinessRoleLoader businessRoleLoader;
    @Autowired(required = false)
    List<PermissionModificationListener> modificationListenerList = new ArrayList<>();

    @PostConstruct
    protected void init() {
        // loads system functions for all modules
        final Set<Subsystem> loadedModules = ModuleRegistry.getLoadedModules();
        for (Subsystem loadedModule : loadedModules) {
            final ISystemFunctionsMapper systemFunctionMapper = loadedModule.getSystemFunctionsMapper();
            // adds to cache all system functions defined in module mapper
            if (systemFunctionMapper != null) {
                for (ISystemFunctionId functionId : systemFunctionMapper.getAllSubsystemFunctions()) {
                    registerSystemFunction(loadedModule, functionId.getName());
                }
            }
            // adds to cache all system functions defined in FH rules
            RuleMetadataRegistry.INSTANCE.getBusinessRules(loadedModule).forEach(aClass -> {
                registerSystemFunction(loadedModule, aClass);
            });
            // adds to cache all system functions defined in FH services
            ServiceMetadataRegistry.INSTANCE.getStaticServices(loadedModule).forEach(aClass -> {
                registerSystemFunction(loadedModule, aClass);
                for (Method method : aClass.getMethods()) {
                    if (!method.isSynthetic()) {
                        registerSystemFunction(loadedModule, method);
                    }
                }
            });
        }

        // adds to cache all system functions defined in FH use cases
        UseCaseMetadataRegistry.INSTANCE.getAll().forEach(useCaseInfo -> {
            registerSystemFunction(useCaseInfo.getSubsystem(), useCaseInfo.getClazz());
            useCaseInfo.getEventsCallback().forEach(useCaseActionInfo -> {
                registerSystemFunction(useCaseInfo.getSubsystem(), useCaseActionInfo.getActionMethodHandler());
            });
        });
    }

    private void readClassInformation(Class clazz) {
        if (clazz.isAnnotationPresent(pl.fhframework.core.uc.UseCase.class)) {
            final Optional<UseCaseInfo> useCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(clazz.getName());
            if (useCaseInfo.isPresent()) {
                Subsystem subsystem = useCaseInfo.get().getSubsystem();
                processClassDefinition(clazz, subsystem);
                processClassMethods(clazz, subsystem);
            }
        }
        else if (clazz.isAnnotationPresent(BusinessRule.class)) {
            Subsystem subsystem = RuleMetadataRegistry.INSTANCE.getSubsystem(clazz.getName());
            processClassDefinition(clazz, subsystem);
            processClassMethods(clazz, subsystem);
        }
        else if (clazz.isAnnotationPresent(FhService.class)) {
            Subsystem subsystem = ServiceMetadataRegistry.INSTANCE.getSubsystem(clazz.getName());
            processClassDefinition(clazz, subsystem);
            processClassMethods(clazz, subsystem);
        }
    }

    private void processClassDefinition(Class clazz, Subsystem subsystem) {
        // System Functions
        Set<Function> functions = new ConcurrentSkipListSet<>();
        getDeclaredFunctions(clazz).forEach(
                functionName -> functions.add(Function.of(functionName, subsystem.getProductUUID()))
        );
        permissionsCache.putFunctionsForClass(clazz, functions);

        // System Roles
        Set<String> roles = getDeclaredRoles(clazz);
        permissionsCache.putRolesForClass(clazz, roles);
    }

    private void processClassMethods(Class clazz, Subsystem subsystem) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isSynthetic()) {
                // System Functions
                Set<Function> functions = new ConcurrentSkipListSet<>();
                getDeclaredFunctions(method).forEach(
                        functionName -> functions.add(Function.of(functionName, subsystem.getProductUUID()))
                );
                permissionsCache.putFunctionsForMethod(method, functions);

                // System Roles
                Set<String> roles = getDeclaredRoles(method);
                permissionsCache.putRolesForMethod(method, roles);
            }
        }
    }

    @Override
    public boolean hasPermission(Collection<IBusinessRole> businessRoles, Class clazz) {
        for (IBusinessRole businessRole : businessRoles) {
            if (hasPermission(businessRole, clazz)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Collection<IBusinessRole> businessRoles, Method method) {
        for (IBusinessRole businessRole : businessRoles) {
            if (hasPermission(businessRole, method)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean hasFunction(Collection<IBusinessRole> businessRoles, String functionName) {
        return hasFunction(businessRoles, functionName, null);
    }

    @Override
    public boolean hasFunction(Collection<IBusinessRole> businessRoles, String functionName, String moduleUUID) {
        final Optional<Function> systemFunction = findSystemFunctionByName(functionName, moduleUUID);
        if (!systemFunction.isPresent()) {
            FhLogger.warn("Cannot find system function by name: {} and module UUID:{}", functionName, moduleUUID);
            return false;
        }
        String functionKey = String.format("%s:%s", functionName, moduleUUID);
        for (IBusinessRole businessRole : businessRoles) {

            Map<String, Boolean> permissions = permissionsCache.getPermissionsForFunction(businessRole);
            if (permissions.containsKey(functionKey)) {
                if (permissions.get(functionKey)) {
                    return true;
                }
            } else {
                final boolean result = isValidSystemFunctionForRole(businessRole, Collections.singletonList(systemFunction.get()));
                permissions.put(functionKey, result);
                permissionsCache.putPermissionsForFunction(businessRole, permissions);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean hasAnyFunction(Collection<IBusinessRole> businessRoles, Collection<String> functionNames) {
        return functionNames.stream().anyMatch(fn -> hasFunction(businessRoles, fn, null));
    }

    @Override
    public boolean hasAnyFunction(Collection<IBusinessRole> businessRoles, Collection<String> functionNames, String moduleUUID) {
        return functionNames.stream().anyMatch(fn -> hasFunction(businessRoles, fn, moduleUUID));
    }

    @Override
    public boolean isRoleBasedAuthorization() {
        return businessRoleLoader != null;
    }

    @Override
    public synchronized void clearPermissions(Collection<IBusinessRole> businessRoles) {
        for (IBusinessRole businessRole : businessRoles) {
            permissionsCache.evictPermissionsForAction(businessRole);
            permissionsCache.evictPermissionsForFunction(businessRole);
        }
    }

    @Override
    public void invalidatePermissionCacheForRole(IBusinessRole businessRole) {
        clearPermissions(Collections.singletonList(businessRole));
        modificationListenerList.forEach(listener -> listener.onRoleChange(businessRole.getRoleName()));
    }

    @Override
    public Set<String> mapSystemFunctionsToBusinessRoles(Set<String> systemFunctions, String moduleUUID) {
        Set<String> roles = new HashSet<>();
        if (isRoleBasedAuthorization()) {
            for (String systemFunction : systemFunctions) {
                roles.addAll(
                        businessRoleLoader.getBusinessRolesForFunction(moduleUUID, systemFunction)
                                .stream()
                                .map(IBusinessRole::getRoleName)
                                .collect(Collectors.toList())
                );
            }

            // system function is required, but no roles provide this function at this moment - expose special value NONE
            // this will let us distinguish no functions required from no roles provide required functions
            if (!systemFunctions.isEmpty() && roles.isEmpty()) {
                roles = new HashSet<>(Arrays.asList(IBusinessRole.NONE));
            }
        }
        return roles;
    }

    private Set<Pair<IBusinessRole, String>> getCachedPairOfBusinessRoles(Map<Pair<IBusinessRole, String>, Boolean> cache, Collection<IBusinessRole> businessRoles) {
        return cache.keySet().stream()
                .filter(pair -> businessRoles.contains(pair.getFirst()))
                .collect(Collectors.toSet());
    }

    protected boolean hasPermission(IBusinessRole businessRole, Method method) {
        Map<String, Boolean> permissions = permissionsCache.getPermissionsForAction(businessRole);
        String key = method.toGenericString();
        if (!permissions.containsKey(key)) {
            final boolean result = calculatePermission(businessRole, method);
            permissions.put(key, result);
            permissions = permissionsCache.putPermissionsForAction(businessRole, permissions);
        }
        return permissions.get(key);
    }

    protected boolean hasPermission(IBusinessRole businessRole, Class clazz) {
        Map<String, Boolean> permissions = permissionsCache.getPermissionsForAction(businessRole);
        String key = clazz.getName();
        if (!permissions.containsKey(key)) {
            final boolean result = calculatePermission(businessRole, clazz);
            permissions.put(key, result);
            permissions = permissionsCache.putPermissionsForAction(businessRole, permissions);
        }
        return permissions.get(key);
    }

    protected boolean calculatePermission(IBusinessRole businessRole, Class clazz) {
        // System Functions
        Set<Function> functions = getAllFunctionsBasedOnClass(clazz);
        if (functions != null) {
            if (isValidSystemFunctionForRole(businessRole, functions)) {
                return true;
            }
        }
        // System Roles
        Set<String> roles = getAllRolesBasedOnClass(clazz);
        if (roles != null) {
            for (String systemRole : roles) {
                if (StringUtils.equals(businessRole.getRoleName(), systemRole)) {
                    return true;
                }
            }
        }

        // If collections are empty that means there is no restriction to execute,
        // otherwise there is no permission to execute
        return CollectionUtils.isEmpty(functions) && CollectionUtils.isEmpty(roles);
    }

    @SuppressWarnings("all")
    protected boolean calculatePermission(IBusinessRole businessRole, Method method) {
        // System Functions
        Set<Function> functions = getAllFunctionsBasedOnMethod(method);
        if (functions != null) {
            if (isValidSystemFunctionForRole(businessRole, functions)) {
                return true;
            }
        }
        // System Roles
        Set<String> roles = getAllRolesBasedOnMethod(method);
        if (roles != null) {
            for (String systemRole : roles) {
                if (StringUtils.equals(businessRole.getRoleName(), systemRole)) {
                    return true;
                }
            }
        }

        // if functions and roles collections are empty then calculate permission for class
        if (CollectionUtils.isEmpty(functions) && CollectionUtils.isEmpty(roles)) {
            return hasPermission(businessRole, method.getDeclaringClass());
        }

        return false;
    }

    private Optional<Function> findSystemFunctionByName(String functionName, String moduleUUID) {
        return ALL_FUNCTIONS_CACHE.stream()
                .filter(function ->
                        StringUtils.equals(function.getName(), functionName) &&
                        (StringUtils.isNullOrEmpty(moduleUUID) || StringUtils.equals(function.getModuleUUID(), moduleUUID)))
                        // TODO warunek isNullOrEmpty należy usunąć po usunięciu metod wyszukujących funkcję po samej nazwie (hasFunction)
                .findAny();
    }

    private Set<Function> getAllFunctionsBasedOnMethod(Method method) {
        Set<Function> functions = permissionsCache.getFunctionsForMethod(method);
        if (functions == null) {
            readClassInformation(method.getDeclaringClass());
            functions = permissionsCache.getFunctionsForMethod(method);
        }
        return functions;
    }

    private Set<Function> getAllFunctionsBasedOnClass(Class clazz) {
        Set<Function> functions = permissionsCache.getFunctionsForClass(clazz);
        if (functions == null) {
            readClassInformation(clazz);
            functions = permissionsCache.getFunctionsForClass(clazz);
        }
        return functions;
    }

    private Set<String> getAllRolesBasedOnMethod(Method method) {
        Set<String> roles = permissionsCache.getRolesForMethod(method);
        if (roles == null) {
            readClassInformation(method.getDeclaringClass());
            roles = permissionsCache.getRolesForMethod(method);
        }
        return roles;
    }

    private Set<String> getAllRolesBasedOnClass(Class clazz) {
        Set<String> roles = permissionsCache.getRolesForClass(clazz);
        if (roles == null) {
            readClassInformation(clazz);
            roles = permissionsCache.getRolesForClass(clazz);
        }
        return roles;
    }

    @Override
    public void registerSystemFunction(Subsystem subsystem, String functionName) {
        Function function = Function.of(functionName, subsystem.getProductUUID());
        if (applicationPrivilegesCache.isValidFunctionForApplication(function)) {
            ALL_FUNCTIONS_CACHE.add(function);
        }
    }

    private void registerSystemFunction(Subsystem subsystem, AnnotatedElement annotatedElement) {
        getDeclaredFunctions(annotatedElement).forEach(
                function -> registerSystemFunction(subsystem, function)
        );
    }

    @Override
    public Set<Function> getAllSystemFunctions() {
        return Collections.unmodifiableSet(ALL_FUNCTIONS_CACHE);
    }

    @Override
    public Set<String> getSystemFunctionForSubsystem(Subsystem subsystem) {
        return ALL_FUNCTIONS_CACHE.stream()
                .filter(function -> function.getModuleUUID().equals(subsystem.getProductUUID()))
                .map(Function::getName)
                .collect(Collectors.toSet());
    }

    public List<Module> getAllModules() {
        return ModuleRegistry.getLoadedModules().stream()
                .map(subsystem -> Module.of(subsystem.getProductLabel(), subsystem.getProductUUID()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private boolean isValidSystemFunctionForRole(IBusinessRole businessRole, Collection<Function> functions) {
        final Collection<IPermission> permissions = businessRoleLoader.getPermissionsForRole(businessRole);
        List<IPermission> allowedPermissions = new ArrayList<>();
        List<IPermission> deniedPermissions = new ArrayList<>();
        permissions.forEach(permission -> {
            if (permission.isDenied()) {
                deniedPermissions.add(permission);
            } else {
                allowedPermissions.add(permission);
            }
        });

        for (Function systemFunction : functions) {
            if (isValidSystemFunctionForRole(systemFunction, allowedPermissions, deniedPermissions)) {
                return true;
            }
        }

        return false;
    }

    private boolean isValidSystemFunctionForRole(Function systemFunction, Collection<IPermission> allowedPermissions,
                                                 Collection<IPermission> deniedPermissions) {
        // check application privileges
        if (!applicationPrivilegesCache.isValidFunctionForApplication(systemFunction)) {
            return false;
        }

        // check denied functions
        for (IPermission permission : deniedPermissions) {
            if (isMatching(permission, systemFunction)) {
                return false;
            }
        }

        // check allowed functions
        for (IPermission permission : allowedPermissions) {
            if (isMatching(permission, systemFunction)) {
                return true;
            }
        }

        return false;
    }


    private boolean isMatching(IPermission permission, Function systemFunction) {
        return Objects.equals(permission.getModuleUUID(), systemFunction.getModuleUUID()) &&
               (systemFunction.getName().equals(permission.getFunctionName()) || systemFunction.getName().startsWith(permission.getFunctionName() + "/"));
    }

    private boolean isGuestRole(IBusinessRole role) {
        return IBusinessRole.GUEST.equals(role.getRoleName());
    }

    @Override
    public boolean hasPermission(Method method) {
        SystemUser user = SessionManager.getSystemUser();

        if (user != null) {
            return hasPermission(user.getBusinessRoles(), method);
        }

        return false;
    }

    @Override
    public boolean hasPermission(Class clazz) {
        SystemUser user = SessionManager.getSystemUser();

        if (user != null) {
            return hasPermission(user.getBusinessRoles(), clazz);
        }

        return false;
    }

    @Override
    public Set<String> getDeclaredFunctions(AnnotatedElement annotatedElement) {
        SystemFunction[] annotations = annotatedElement.getAnnotationsByType(SystemFunction.class);
        return Arrays.stream(annotations)
                .map(SystemFunction::value)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getDeclaredRoles(AnnotatedElement annotatedElement) {
        SystemRole[] annotations = annotatedElement.getAnnotationsByType(SystemRole.class);
        return Arrays.stream(annotations)
                .map(SystemRole::value)
                .collect(Collectors.toSet());
    }

}
