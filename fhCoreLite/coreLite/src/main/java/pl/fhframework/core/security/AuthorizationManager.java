package pl.fhframework.core.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Core fh interfaces of security manager. Fh provides basic implementation named {@link
 * CoreAuthorizationManager}. This manager can be optional and work as a separate module that can be
 * plugged in.
 */
public interface AuthorizationManager extends PermissionsProvider {

    /**
     * Checks if given business roles(subject, probably user roles) has permission to action or use
     * case.
     *
     * @param businessRoles  - business roles
     * @param method - action
     * @return true if any role has access, false otherwise
     */
    boolean hasPermission(Collection<IBusinessRole> businessRoles, Method method);

    /**
     * Checks if given business roles(subject, probably user roles) has permission to action or use
     * case.
     *
     * @param businessRoles  - business roles
     * @param clazz - use case
     * @return true if any role has access, false otherwise
     */
    boolean hasPermission(Collection<IBusinessRole> businessRoles, Class clazz);

    /**
     * Checks if given business roles(subject, probably user roles) has mapped system function.
     *
     * @deprecated use {@link #hasFunction(Collection, String, String)}
     *
     * @param businessRoles - business roles
     * @param functionName  - system function's name
     * @return true if any role has mapped system function, false otherwise
     */
    @Deprecated
    boolean hasFunction(Collection<IBusinessRole> businessRoles, String functionName);

    /**
     * Checks if given business roles(subject, probably user roles) has mapped system function for given module.
     *
     * @param businessRoles - business roles
     * @param functionName  - system function's name
     * @param moduleUUID - a unique identification of an application module
     * @return true if any role has mapped system function, false otherwise
     */
    boolean hasFunction(Collection<IBusinessRole> businessRoles, String functionName, String moduleUUID);

    /**
     * Checks if given business roles(subject, probably user roles) has mapped any of system functions.
     *
     * @deprecated use {@link #hasAnyFunction(Collection, Collection, String)}
     *
     * @param businessRoles - business roles
     * @param functionNames  - system function's names
     * @return true if any role has mapped to any system function, false otherwise
     */
    @Deprecated
    boolean hasAnyFunction(Collection<IBusinessRole> businessRoles, Collection<String> functionNames);

    /**
     * Checks if given business roles(subject, probably user roles) has mapped any of system functions.
     *
     * @param businessRoles - business roles
     * @param functionNames  - system function's names
     * @param moduleUUID - a unique identification of an application module
     * @return true if any role has mapped to any system function, false otherwise
     */
    boolean hasAnyFunction(Collection<IBusinessRole> businessRoles, Collection<String> functionNames, String moduleUUID);

    /**
     * If role based authorization is enabled (BusinessRoleProvider exists).
     *
     * @return if role based authorization is enabled (BusinessRoleProvider exists)
     */
    boolean isRoleBasedAuthorization();
    /**
     * Method provides possibility of clearing cached result of calculated permission info for
     * business roles.
     *
     * @param businessRoles - roles for which permissions will be removed from cache
     */
    void clearPermissions(Collection<IBusinessRole> businessRoles);

    /**
     * Maps given system functions from given module to business roles that provide them.
     * May return IBusinessRole.NONE if none of roles map to given system function.
     * @param systemFunctions system functions
     * @param moduleUUID module UUID
     * @return set of business roles
     */
    Set<String> mapSystemFunctionsToBusinessRoles(Set<String> systemFunctions, String moduleUUID);

    /**
     * Adds system function to authorization cache.
     * @param subsystem subsystem where the function was registered
     * @param functionName system function name
     */
    void registerSystemFunction(Subsystem subsystem, String functionName);

    /**
     * Gets all system functions for given subsystem.
     * @param subsystem representation of a subsystem.
     * @return set of system functions for given subsystem
     */
    Set<String> getSystemFunctionForSubsystem(Subsystem subsystem);

    /**
     * Returns all registered modules
     * @return set of modules
     */
    List<Module> getAllModules();

    /**
     * Returns all registered system functions
     * @return set of registered system functions
     */
    Set<Function> getAllSystemFunctions();

    /**
     * Invalidates information about permissions for given business role.
     * It makes authorization manager to recalculate permission for business
     * role during next access to use case.
     * @param businessRole business role
     */
    void invalidatePermissionCacheForRole(IBusinessRole businessRole);

    @Getter
    @EqualsAndHashCode(of = {"name", "moduleUUID"})
    class Function implements IFunction, Comparable<Function>, Serializable {
        private String name;
        private String moduleUUID;
        private String moduleLabel;
        private boolean denial;

        private Function() {
        }

        public static Function of(String functionName, String moduleUUID) {
            return of(functionName, moduleUUID, ModuleRegistry.getModuleProductLabel(moduleUUID));
        }

        public static Function of(String functionName, String moduleUUID, String moduleLabel) {
            return of(functionName, moduleUUID, moduleLabel, false);
        }

        public static Function of(String functionName, String moduleUUID, String moduleLabel, boolean denial) {
            Function function = new Function();
            function.name = functionName;
            function.moduleUUID = moduleUUID;
            function.moduleLabel = moduleLabel;
            function.denial = denial;
            return function;
        }

        @Override
        public int compareTo(Function function) {
            if (function != null) {
                int result = name.compareTo(function.name);
                if (result != 0) return result;
                return moduleUUID.compareTo(function.moduleUUID);
            }
            return 0;
        }
    }

    @Getter
    @EqualsAndHashCode(of = "uuid")
    class Module implements IModule, Comparable<Module>, Serializable {
        private String name;
        private String uuid;

        private Module() {
        }

        public static Module of(String name, String uuid) {
            Module module = new Module();
            module.name = name;
            module.uuid = uuid;
            return module;
        }

        @Override
        public int compareTo(Module module) {
            if (module != null) {
                return name.compareTo(module.name);
            }
            return 0;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
