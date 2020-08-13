package pl.fhframework.core.security;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import pl.fhframework.core.security.model.IBusinessRole;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tomasz.Kozlowski (created on 17.01.2019)
 */
@Component
public class ApplicationPermissionsCache {

    //==================================================================================================================
    // HAS_PERMISSION_FOR_ACTION_CACHE

    /** Results of permission calculate for classes and actions */
    private static final String HAS_PERMISSION_FOR_ACTION_CACHE = "has-permission-for-action";

    @Cacheable(value = HAS_PERMISSION_FOR_ACTION_CACHE, key = "#businessRole.roleName.toUpperCase()")
    public Map<String, Boolean> getPermissionsForAction(IBusinessRole businessRole) {
        return new ConcurrentHashMap<>();
    }

    @CachePut(value = HAS_PERMISSION_FOR_ACTION_CACHE, key = "#businessRole.roleName.toUpperCase()")
    public  Map<String, Boolean> putPermissionsForAction(IBusinessRole businessRole, Map<String,Boolean> permissions) {
        return permissions;
    }

    @CacheEvict(value = HAS_PERMISSION_FOR_ACTION_CACHE, key = "#businessRole.roleName.toUpperCase()")
    public void evictPermissionsForAction(IBusinessRole businessRole) {
        // evict cache for given business role
    }

    //==================================================================================================================
    // HAS_PERMISSION_FOR_FUNCTION_CACHE

    /** Results of permission calculate for system functions */
    private static final String HAS_PERMISSION_FOR_FUNCTION_CACHE = "has-permission-for-function";

    @Cacheable(value = HAS_PERMISSION_FOR_FUNCTION_CACHE, key = "#businessRole.roleName.toUpperCase()")
    public Map<String, Boolean> getPermissionsForFunction(IBusinessRole businessRole) {
        return new ConcurrentHashMap<>();
    }

    @CachePut(value = HAS_PERMISSION_FOR_FUNCTION_CACHE, key = "#businessRole.roleName.toUpperCase()")
    @SuppressWarnings("UnusedReturnValue")
    public  Map<String, Boolean> putPermissionsForFunction(IBusinessRole businessRole, Map<String,Boolean> permissions) {
        return permissions;
    }

    @CacheEvict(value = HAS_PERMISSION_FOR_FUNCTION_CACHE, key = "#businessRole.roleName.toUpperCase()")
    public void evictPermissionsForFunction(IBusinessRole businessRole) {
        // evict cache for given business role
    }

    //==================================================================================================================
    // CLASS_FUNCTIONS_CACHE

    /** FH components classes mapped to system functions */
    private static final String CLASS_FUNCTIONS_CACHE = "class-functions";

    @Cacheable(value = CLASS_FUNCTIONS_CACHE, key = "#clazz.name")
    public Set<AuthorizationManager.Function> getFunctionsForClass(Class clazz) {
        return null;
    }

    @CachePut(value = CLASS_FUNCTIONS_CACHE, key = "#clazz.name")
    @SuppressWarnings("UnusedReturnValue")
    public  Set<AuthorizationManager.Function> putFunctionsForClass(Class clazz, Set<AuthorizationManager.Function> functions) {
        return functions;
    }

    @CacheEvict(value = CLASS_FUNCTIONS_CACHE, key = "#clazz.name")
    @SuppressWarnings("unused")
    public void evictFunctionsForClass(Class clazz) {
        // evict cache for given class
    }

    //==================================================================================================================
    // CLASS_ROLES_CACHE

    /** FH components classes mapped to system roles */
    private static final String CLASS_ROLES_CACHE = "class-roles";

    @Cacheable(value = CLASS_ROLES_CACHE, key = "#clazz.name")
    public Set<String> getRolesForClass(Class clazz) {
        return null;
    }

    @CachePut(value = CLASS_ROLES_CACHE, key = "#clazz.name")
    @SuppressWarnings("UnusedReturnValue")
    public  Set<String> putRolesForClass(Class clazz, Set<String> roles) {
        return roles;
    }

    @CacheEvict(value = CLASS_ROLES_CACHE, key = "#clazz.name")
    @SuppressWarnings("unused")
    public void evictRolesForClass(Class clazz) {
        // evict cache for given class
    }

    //==================================================================================================================
    // METHOD_FUNCTIONS_CACHE

    /** FH components methods mapped to system functions */
    private static final String METHOD_FUNCTIONS_CACHE = "method-functions";

    @Cacheable(value = METHOD_FUNCTIONS_CACHE, key = "#method.toGenericString()")
    @SuppressWarnings("unused")
    public Set<AuthorizationManager.Function> getFunctionsForMethod(Method method) {
        return null;
    }

    @CachePut(value = METHOD_FUNCTIONS_CACHE, key = "#method.toGenericString()")
    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public  Set<AuthorizationManager.Function> putFunctionsForMethod(Method method, Set<AuthorizationManager.Function> functions) {
        return functions;
    }

    @CacheEvict(value = METHOD_FUNCTIONS_CACHE, key = "#method.toGenericString()")
    @SuppressWarnings("unused")
    public void evictFunctionsForMethod(Method method) {
        // evict cache for given class
    }

    //==================================================================================================================
    // METHOD_ROLES_CACHE

    /** FH components methods mapped to system roles */
    private static final String METHOD_ROLES_CACHE = "method-roles";

    @Cacheable(value = METHOD_ROLES_CACHE, key = "#method.toGenericString()")
    @SuppressWarnings("unused")
    public Set<String> getRolesForMethod(Method method) {
        return null;
    }

    @CachePut(value = METHOD_ROLES_CACHE, key = "#method.toGenericString()")
    @SuppressWarnings({"UnusedReturnValue", "unused"})
    public  Set<String> putRolesForMethod(Method method, Set<String> roles) {
        return roles;
    }

    @CacheEvict(value = METHOD_ROLES_CACHE, key = "#method.toGenericString()")
    @SuppressWarnings("unused")
    public void evictRolesForMethod(Method method) {
        // evict cache for given class
    }

    //==================================================================================================================

}
