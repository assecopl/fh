package pl.fhframework.core.security.provider.ldap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.core.security.provider.service.UserAccountProvider;

import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2018-06-13)
 */
@Service
public class LDAPUserAccountProvider implements UserAccountProvider {

    private static final String MESSAGE = "LDAP Security Data Provider does not support this operation";
    private static final String PROVIDER_TYPE = "LDAP";

    @Autowired
    private LdapContextSource ldapContextSource;

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
    public IUserAccount findUserAccountByLogin(String login) {
        return null;
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
    public boolean supportsUserManagement() {
        return false;
    }

    @Override
    public String getUserAccountProviderType() {
        return PROVIDER_TYPE;
    }

    @Override
    public String getUserAccountProviderSource() {
        String[] urls = ldapContextSource.getUrls();
        String ldapUrl;
        if (urls != null && urls.length > 0) {
            ldapUrl = urls[0];
        } else {
            ldapUrl = "...";
        }
        return String.format("%s/%s", ldapUrl, ldapContextSource.getBaseLdapName());
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
