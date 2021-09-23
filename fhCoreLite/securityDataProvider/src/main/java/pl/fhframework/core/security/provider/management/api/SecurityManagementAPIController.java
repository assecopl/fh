package pl.fhframework.core.security.provider.management.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IPermission;

import java.util.List;
import java.util.Set;

/**
 * @author tomasz.kozlowski (created on 2018-05-14)
 */
@RestController
@ConditionalOnProperty(name = "fhframework.securitymanagementApi.enabled", havingValue = "true")
public class SecurityManagementAPIController {

    // SECURITY API
    public static final String SECURITY_MANAGEMENT_API_URI = "/securityAPI";
    // ROLES
    public static final String SECURITY_MANAGEMENT_API_ROLES_FIND_URI = SECURITY_MANAGEMENT_API_URI + "/roles/find/{roleName}";
    public static final String SECURITY_MANAGEMENT_API_ROLES_LIST_URI = SECURITY_MANAGEMENT_API_URI + "/roles/list";
    public static final String SECURITY_MANAGEMENT_API_ROLES_SUPPORTS_URI = SECURITY_MANAGEMENT_API_URI + "/roles/supports";
    // PERMISSIONS
    public static final String SECURITY_MANAGEMENT_API_PERMISSIONS_CREATE_URI = SECURITY_MANAGEMENT_API_URI + "/permissions/create";
    public static final String SECURITY_MANAGEMENT_API_PERMISSIONS_SAVE_URI = SECURITY_MANAGEMENT_API_URI + "/permissions/save";
    public static final String SECURITY_MANAGEMENT_API_PERMISSIONS_DELETE_URI = SECURITY_MANAGEMENT_API_URI + "/permissions/delete";
    public static final String SECURITY_MANAGEMENT_API_PERMISSIONS_FIND_URI = SECURITY_MANAGEMENT_API_URI + "/permissions/find/{roleName}";
    // AUTHORIZATION CACHE
    public static final String SECURITY_MANAGEMENT_API_CACHE_FUNCTIONS_URI = SECURITY_MANAGEMENT_API_URI + "/cache/functions";
    public static final String SECURITY_MANAGEMENT_API_CACHE_MODULES_URI = SECURITY_MANAGEMENT_API_URI + "/cache/modules";
    public static final String SECURITY_MANAGEMENT_API_CACHE_INVALIDATE_URI = SECURITY_MANAGEMENT_API_URI + "/cache/invalidate/{roleName}";
    // PARAMS
    public static final String SECURITY_MANAGEMENT_API_ROLE_NAME = "roleName";
    public static final String SECURITY_MANAGEMENT_API_FUNCTION_NAME = "functionName";
    public static final String SECURITY_MANAGEMENT_API_MODULE_UUID = "moduleUUID";

    @Autowired
    private ISecurityDataProvider securityDataProvider;

    @GetMapping(SECURITY_MANAGEMENT_API_ROLES_FIND_URI)
    public IBusinessRole findBusinessRoleByName(@PathVariable(SECURITY_MANAGEMENT_API_ROLE_NAME) String roleName) {
        return securityDataProvider.findBusinessRoleByName(roleName);
    }

    @GetMapping(SECURITY_MANAGEMENT_API_ROLES_LIST_URI)
    public List<IBusinessRole> findAllBusinessRoles() {
        return securityDataProvider.findAllBusinessRoles();
    }

    @GetMapping(SECURITY_MANAGEMENT_API_ROLES_SUPPORTS_URI)
    public Boolean supportsRoleManagement() {
        return securityDataProvider.supportsRoleManagement();
    }

    @PostMapping(SECURITY_MANAGEMENT_API_PERMISSIONS_CREATE_URI)
    public IPermission createSimplePermissionInstance(@RequestParam(SECURITY_MANAGEMENT_API_ROLE_NAME) String businessRoleName,
                                                      @RequestParam(SECURITY_MANAGEMENT_API_FUNCTION_NAME) String functionName,
                                                      @RequestParam(SECURITY_MANAGEMENT_API_MODULE_UUID) String moduleUUID) {
        return securityDataProvider.createSimplePermissionInstance(businessRoleName, functionName, moduleUUID);
    }

    @PostMapping(SECURITY_MANAGEMENT_API_PERMISSIONS_SAVE_URI)
    public void savePermissions(@RequestBody List<IPermission> permissions) {
        securityDataProvider.savePermissions(permissions);
    }

    @PostMapping(value = SECURITY_MANAGEMENT_API_PERMISSIONS_DELETE_URI)
    public void deletePermission(@RequestBody IPermission permission) {
        securityDataProvider.deletePermission(permission);
    }

    @GetMapping(SECURITY_MANAGEMENT_API_PERMISSIONS_FIND_URI)
    public List<IPermission> findPermissionsForRole(@PathVariable(SECURITY_MANAGEMENT_API_ROLE_NAME) String roleName) {
        IBusinessRole businessRole = securityDataProvider.findBusinessRoleByName(roleName);
        return securityDataProvider.findPermissionsForRole(businessRole);
    }

    @GetMapping(SECURITY_MANAGEMENT_API_CACHE_FUNCTIONS_URI)
    public Set<AuthorizationManager.Function> getAllSystemFunctions() {
        return securityDataProvider.getAllSystemFunctions();
    }

    @GetMapping(SECURITY_MANAGEMENT_API_CACHE_MODULES_URI)
    public List<AuthorizationManager.Module> getAllModules() {
        return securityDataProvider.getAllModules();
    }

    @GetMapping(SECURITY_MANAGEMENT_API_CACHE_INVALIDATE_URI)
    public void invalidatePermissionCacheForRole(@PathVariable(SECURITY_MANAGEMENT_API_ROLE_NAME) String roleName) {
        IBusinessRole businessRole = securityDataProvider.findBusinessRoleByName(roleName);
        securityDataProvider.invalidatePermissionCacheForRole(businessRole);
    }

}
