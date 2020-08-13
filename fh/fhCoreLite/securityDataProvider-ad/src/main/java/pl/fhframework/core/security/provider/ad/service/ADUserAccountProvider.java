package pl.fhframework.core.security.provider.ad.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.core.security.provider.service.UserAccountProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ADUserAccountProvider implements UserAccountProvider {
    private static final String PROVIDER_TYPE = "AD";

    @Override
    public IUserAccount createSimpleUserAccountInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IRoleInstance createSimpleRoleInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IUserAccount saveUserAccount(IUserAccount userAccount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveUserAccounts(List<IUserAccount> userAccounts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IUserAccount findUserAccountByLogin(String login) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean changeUserPassword(String login, String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<IUserAccount> findAllUserAccounts(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<IUserAccount> findAllUserAccounts(IUserAccount probe, Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IUserAccount> findAllUserAccounts() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getUserAccountsCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportsUserManagement() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUserAccountProviderType() {
        return PROVIDER_TYPE;
    }

    @Override
    public String getUserAccountProviderSource() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void detachRoleFromUsers(IBusinessRole businessRole) {
        throw new UnsupportedOperationException();
    }
}
