package pl.fhframework.core.security.provider.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.ISystemFunctionId;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.core.security.provider.exception.SecurityDataProviderException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tomasz.kozlowski (created on 2018-03-09)
 */
@Service
@RequiredArgsConstructor
public class SecurityDataProvider implements ISecurityDataProvider {

    @NonNull
    private final UserAccountProvider userAccountProvider;
    @NonNull
    private final BusinessRoleProvider businessRoleProvider;
    @NonNull
    private final PermissionProvider permissionProvider;
    @NonNull
    private final LocalServerDefinitionUpdateInformer changesInformer;

    private AuthorizationManager authorizationManager;

    @Autowired // inject by setter to avoid dependency cycle
    public void setAuthorizationManager(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    // USERS
    //====================================================================================

    @Override
    public IUserAccount saveUserAccount(IUserAccount userAccount) {
        return userAccountProvider.saveUserAccount(userAccount);
    }

    @Override
    public void saveUserAccounts(List<IUserAccount> userAccounts) {
        userAccountProvider.saveUserAccounts(userAccounts);
    }

    @Override
    public IUserAccount findUserAccountByLogin(String login) {
        return userAccountProvider.findUserAccountByLogin(login);
    }

    @Override
    public boolean changeUserPassword(String login, String oldPassword, String newPassword) {
        return userAccountProvider.changeUserPassword(login, oldPassword, newPassword);
    }

    @Override
    public Page<IUserAccount> findAllUserAccounts(IUserAccount probe, Pageable pageable) {
        return userAccountProvider.findAllUserAccounts(probe, pageable);
    }

    @Override
    public Page<IUserAccount> findAllUserAccounts(Pageable pageable) {
        return  userAccountProvider.findAllUserAccounts(pageable);
    }

    @Override
    public long getUserAccountsCount() {
        return userAccountProvider.getUserAccountsCount();
    }

    @Override
    public boolean supportsUserManagement() {
        return userAccountProvider.supportsUserManagement();
    }

    @Override
    public IUserAccount createSimpleUserAccountInstance() {
        return userAccountProvider.createSimpleUserAccountInstance();
    }

    @Override
    public String getUserAccountProviderType() {
        return userAccountProvider.getUserAccountProviderType();
    }

    @Override
    public String getUserAccountProviderSource() {
        return userAccountProvider.getUserAccountProviderSource();
    }

    // ROLES
    //====================================================================================

    @Override
    public IBusinessRole createSimpleBusinessRoleInstance(String roleName) {
        return businessRoleProvider.createSimpleBusinessRoleInstance(roleName);
    }

    @Override
    public IRoleInstance createSimpleRoleInstance() {
        return userAccountProvider.createSimpleRoleInstance();
    }

    @Override
    @Transactional
    public IBusinessRole saveBusinessRole(IBusinessRole businessRole) {
        if (businessRole.getId() != null) {
            IBusinessRole oldRole = businessRoleProvider.findBusinessRoleWithoutConversation(businessRole.getId());
            if (!oldRole.getRoleName().equals(businessRole.getRoleName())) {
                // update permissions when role changes its name
                permissionProvider.updatePermissionsForRole(oldRole.getRoleName(), businessRole.getRoleName());
            }
        }
        IBusinessRole result = businessRoleProvider.saveBusinessRole(businessRole);
        changesInformer.informServerDefinitionChanged();
        return result;
    }

    @Override
    public void saveBusinessRoles(List<IBusinessRole> roles) {
        businessRoleProvider.saveBusinessRoles(roles);
        changesInformer.informServerDefinitionChanged();
    }

    @Override
    @Transactional
    public void deleteBusinessRole(IBusinessRole businessRole) {
        if (userAccountProvider.supportsUserManagement()) {
            // if user management is supported then detach business role from users
            userAccountProvider.detachRoleFromUsers(businessRole);
        }
        permissionProvider.deletePermissionsForRole(businessRole);
        businessRoleProvider.deleteBusinessRole(businessRole);
        changesInformer.informServerDefinitionChanged();
    }

    @Override
    public IBusinessRole findBusinessRoleByName(String roleName) {
        return businessRoleProvider.findBusinessRoleByName(roleName);
    }

    @Override
    public List<IBusinessRole> findBusinessRolesForUser(String login) {
        IUserAccount userAccount = userAccountProvider.findUserAccountByLogin(login);
        if(userAccount == null) {
            throw new SecurityDataProviderException("Cannot find user account by login: " + login);
        }

        return userAccount.getRoles().stream()
                .map(IRoleInstance::getBusinessRole)
                .collect(Collectors.toList());
    }

    @Override
    public List<IBusinessRole> findBusinessRolesForFunction(String moduleUUID, String functionName) {
        Collection<String> functions = getFunctionHierarchy(functionName);
        List<IPermission> permissions = permissionProvider.findByModuleUUIDAndFunctionNameIn(moduleUUID, functions);
        return permissions.stream()
                .map(permission -> businessRoleProvider.findBusinessRoleByName(permission.getBusinessRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<IBusinessRole> findAllBusinessRoles() {
        return businessRoleProvider.findAllBusinessRoles();
    }

    @Override
    public boolean supportsRoleManagement() {
        return businessRoleProvider.supportsRoleManagement();
    }

    private Set<String> getFunctionHierarchy(String templateFunction) {
        Set<String> functions = new HashSet<>();
        StringTokenizer tokenizer = new StringTokenizer(templateFunction, ISystemFunctionId.SEPARATOR);
        String functionName = "";
        while (tokenizer.hasMoreTokens()) {
            functionName += (!functionName.isEmpty() ? ISystemFunctionId.SEPARATOR : "") + tokenizer.nextToken();
            functions.add(functionName);
        }
        return functions;
    }

    @Override
    public String getBusinessRoleProviderType() {
        return businessRoleProvider.getBusinessRoleProviderType();
    }

    @Override
    public String getBusinessRoleProviderSource() {
        return businessRoleProvider.getBusinessRoleProviderSource();
    }

    // PERMISSIONS
    //====================================================================================

    @Override
    public IPermission createSimplePermissionInstance(String businessRoleName, String functionName, String moduleUUID) {
        return permissionProvider.createSimplePermissionInstance(businessRoleName, functionName, moduleUUID);
    }

    @Override
    public IPermission savePermission(IPermission permission) {
        IPermission result = permissionProvider.savePermission(permission);
        changesInformer.informServerDefinitionChanged();
        return result;
    }

    @Override
    public void savePermissions(List<IPermission> permissions) {
        if (!CollectionUtils.isEmpty(permissions)) {
            permissions.forEach(permissionProvider::savePermission);
        }
        changesInformer.informServerDefinitionChanged();
    }

    @Override
    public void deletePermission(IPermission permission) {
        permissionProvider.deletePermission(permission);
        changesInformer.informServerDefinitionChanged();
    }

    @Override
    public List<IPermission> findPermissionsForRole(IBusinessRole businessRole) {
        return permissionProvider.findPermissionsForRole(businessRole);
    }

    // AUTHORIZATION MANAGER
    //====================================================================================

    @Override
    public Set<AuthorizationManager.Function> getAllSystemFunctions() {
        return authorizationManager.getAllSystemFunctions();
    }

    @Override
    public List<AuthorizationManager.Module> getAllModules() {
        return authorizationManager.getAllModules();
    }

    @Override
    public void invalidatePermissionCacheForRole(IBusinessRole businessRole) {
        authorizationManager.invalidatePermissionCacheForRole(businessRole);
        changesInformer.informServerDefinitionChanged();
    }

}
