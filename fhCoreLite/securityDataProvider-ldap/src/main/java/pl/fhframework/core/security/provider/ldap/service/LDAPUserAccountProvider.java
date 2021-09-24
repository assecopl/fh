package pl.fhframework.core.security.provider.ldap.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.core.security.provider.ldap.model.UserAccount;
import pl.fhframework.core.security.provider.service.UserAccountProvider;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2018-06-13)
 */
@Service
@RequiredArgsConstructor
public class LDAPUserAccountProvider implements UserAccountProvider {

    private static final String MESSAGE = "LDAP Security Data Provider does not support this operation";
    private static final String PROVIDER_TYPE = "LDAP";

    @Value("${fhframework.security.provider.ldap.user-base}")
    private String userBase = "ou=users";

    private final LdapContextSource ldapContextSource;
    private final LdapTemplate ldapTemplate;

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
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("objectclass", "person"));
        filter.and(new EqualsFilter("cn", login));

        List<IUserAccount> users = ldapTemplate.search(
                userBase,
                filter.encode(),
                new UserAccountAttributeMapper());

        return users.size() > 0 ? users.get(0) : null;
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
    @Transactional(readOnly = true)
    public List<IUserAccount> findAllUserAccounts() {
        return ldapTemplate.search(
                userBase, "(objectclass=person)",
                new UserAccountAttributeMapper());
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

    // Business role attribute mapper class =============================================================
    private class UserAccountAttributeMapper implements AttributesMapper<IUserAccount> {
        @Override
        public IUserAccount mapFromAttributes(Attributes attributes) throws NamingException {
            UserAccount userAccount = new UserAccount();
            userAccount.setLogin((String)attributes.get("cn").get());
            userAccount.setFirstName((String)attributes.get("givenName").get());
            userAccount.setLastName((String)attributes.get("sn").get());
            userAccount.setEmail((String)attributes.get("mail").get());
            return userAccount;
        }
    }

}
