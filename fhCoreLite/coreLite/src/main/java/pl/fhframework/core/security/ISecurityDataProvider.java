package pl.fhframework.core.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.subsystems.Subsystem;

import java.util.List;
import java.util.Set;

/**
 * An interface of a security data provider
 * @author tomasz.kozlowski (created on 2017-11-22)
 */
public interface ISecurityDataProvider {

    // USERS
    IUserAccount createSimpleUserAccountInstance();
    IUserAccount saveUserAccount(IUserAccount userAccount);
    void saveUserAccounts(List<IUserAccount> userAccounts);
    IUserAccount findUserAccountByLogin(String login);
    boolean changeUserPassword(String login, String oldPassword, String newPassword);
    Page<IUserAccount> findAllUserAccounts(Pageable pageable);
    Page<IUserAccount> findAllUserAccounts(IUserAccount probe, Pageable pageable);
    long getUserAccountsCount();
    boolean supportsUserManagement();
    String getUserAccountProviderType();
    String getUserAccountProviderSource();

    // ROLES
    IBusinessRole createSimpleBusinessRoleInstance(String roleName);
    IRoleInstance createSimpleRoleInstance();
    IBusinessRole saveBusinessRole(IBusinessRole businessRole);
    void saveBusinessRoles(List<IBusinessRole> roles);
    void deleteBusinessRole(IBusinessRole businessRole);
    IBusinessRole findBusinessRoleByName(String roleName);
    List<IBusinessRole> findBusinessRolesForUser(String login);
    List<IBusinessRole> findBusinessRolesForFunction(String moduleUUID, String functionName);
    List<IBusinessRole> findAllBusinessRoles();
    boolean supportsRoleManagement();
    String getBusinessRoleProviderType();
    String getBusinessRoleProviderSource();

    // PERMISSIONS
    IPermission createSimplePermissionInstance(String businessRoleName, String functionName, String moduleUUID);
    IPermission savePermission(IPermission permission);
    void savePermissions(List<IPermission> permissions);
    void deletePermission(IPermission permission);
    List<IPermission> findPermissionsForRole(IBusinessRole businessRole);

    // AUTHORIZATION MANAGER
    Set<AuthorizationManager.Function> getAllSystemFunctions();
    List<AuthorizationManager.Module> getAllModules();
    void invalidatePermissionCacheForRole(IBusinessRole businessRole);

}
