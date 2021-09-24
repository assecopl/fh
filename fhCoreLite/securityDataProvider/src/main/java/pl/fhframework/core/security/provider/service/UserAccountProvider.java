package pl.fhframework.core.security.provider.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;

import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2018-04-20)
 */
public interface UserAccountProvider {

    IUserAccount createSimpleUserAccountInstance();

    IRoleInstance createSimpleRoleInstance();

    IUserAccount saveUserAccount(IUserAccount userAccount);

    void saveUserAccounts(List<IUserAccount> userAccounts);

    IUserAccount findUserAccountByLogin(String login);

    boolean changeUserPassword(String login, String oldPassword, String newPassword);

    Page<IUserAccount> findAllUserAccounts(Pageable pageable);

    Page<IUserAccount> findAllUserAccounts(IUserAccount probe, Pageable pageable);

    List<IUserAccount> findAllUserAccounts();

    long getUserAccountsCount();

    boolean supportsUserManagement();

    String getUserAccountProviderType();

    String getUserAccountProviderSource();

    void detachRoleFromUsers(IBusinessRole businessRole);

}
