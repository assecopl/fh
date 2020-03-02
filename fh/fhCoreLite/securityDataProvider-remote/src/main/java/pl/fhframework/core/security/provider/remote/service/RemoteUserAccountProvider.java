package pl.fhframework.core.security.provider.remote.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import pl.fhframework.core.security.UserAttributesTempCache;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.core.security.provider.remote.model.BusinessRole;
import pl.fhframework.core.security.provider.remote.model.RoleInstance;
import pl.fhframework.core.security.provider.remote.model.UserAccount;
import pl.fhframework.core.security.provider.remote.model.UserInfo;
import pl.fhframework.core.security.provider.remote.repository.RemoteRepositoryManager;
import pl.fhframework.core.security.provider.service.UserAccountProvider;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomasz Kozlowski (created on 20.05.2019)
 */
@Service
public class RemoteUserAccountProvider implements UserAccountProvider {

    private RemoteRepositoryManager repositoryManager;
    private UserAttributesTempCache userAttributesTempCache;

    @Autowired
    public void setRepositoryManager(RemoteRepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Autowired
    public void setUserAttributesTempCache(UserAttributesTempCache userAttributesTempCache) {
        this.userAttributesTempCache = userAttributesTempCache;
    }

    @Override
    public IUserAccount findUserAccountByLogin(String login) {
        UserInfo userInfo = repositoryManager.findUserByName(login);
        if (userInfo == null) {
            throw new IllegalStateException("user " + login + " not found");
        }

        // convert to user account
        UserAccount userAccount = convert(userInfo);

        // store attributes
        if (!CollectionUtils.isEmpty(userInfo.getAttributes())) {
            userAttributesTempCache.putForUser(userInfo.getUsername(), userInfo.getAttributes());
        }

        return userAccount;
    }

    @Transactional(readOnly = true)
    public List<IUserAccount> findAllUserAccounts() {
        List<UserInfo> users = repositoryManager.findAllUsers();
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        } else {
            return users.stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
        }
    }

    private UserAccount convert(UserInfo userInfo) {
        // user
        UserAccount userAccount = new UserAccount();
        userAccount.setLogin(userInfo.getUsername());
        userAccount.setFirstName(userInfo.getFirstName());
        userAccount.setLastName(userInfo.getLastName());
        userAccount.setPassword(userInfo.getPassword());
        userAccount.setBlocked(userInfo.isBlocked());
        userAccount.setDeleted(userInfo.isDeleted());
        // roles
        if (!CollectionUtils.isEmpty(userInfo.getRoles())) {
            userAccount.setRoleInstances(
                    userInfo.getRoles().stream()
                            .map(this::createRoleInstance)
                            .collect(Collectors.toList())
            );
        }
        return userAccount;
    }

    private RoleInstance createRoleInstance(String roleName) {
        RoleInstance roleInstance = new RoleInstance();
        BusinessRole businessRole = new BusinessRole(roleName);
        roleInstance.setBusinessRole(businessRole);
        return roleInstance;
    }

    @Override
    public boolean supportsUserManagement() {
        return false;
    }

    @Override
    public String getUserAccountProviderType() {
        return "Remote";
    }

    @Override
    public String getUserAccountProviderSource() {
        return repositoryManager.getRepositorySource();
    }

    // Unsupported operations ===========================================================================
    private static final String MESSAGE = "Remote Security Data Provider does not support this operation";

    @Override
    public IUserAccount createSimpleUserAccountInstance() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public IUserAccount saveUserAccount(IUserAccount userAccount) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void saveUserAccounts(List<IUserAccount> userAccounts) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public boolean changeUserPassword(String login, String oldPassword, String newPassword) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public Page<IUserAccount> findAllUserAccounts(Pageable pageable) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public Page<IUserAccount> findAllUserAccounts(IUserAccount probe, Pageable pageable) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public long getUserAccountsCount() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public IRoleInstance createSimpleRoleInstance() {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void detachRoleFromUsers(IBusinessRole businessRole) {
        throw new UnsupportedOperationException(MESSAGE);
    }

}
