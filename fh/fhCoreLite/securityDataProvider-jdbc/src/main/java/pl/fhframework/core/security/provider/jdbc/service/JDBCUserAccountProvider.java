package pl.fhframework.core.security.provider.jdbc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.core.security.provider.jdbc.model.BusinessRole;
import pl.fhframework.core.security.provider.jdbc.model.RoleInstance;
import pl.fhframework.core.security.provider.jdbc.model.UserAccount;
import pl.fhframework.core.security.provider.jdbc.repository.RoleInstanceRepository;
import pl.fhframework.core.security.provider.jdbc.repository.UserAccountRepository;
import pl.fhframework.core.security.provider.service.UserAccountProvider;
import pl.fhframework.core.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2018-04-20)
 */
@Service
@RequiredArgsConstructor
public class JDBCUserAccountProvider implements UserAccountProvider {

    private static final String PROVIDER_TYPE = "JDBC";

    private final UserAccountRepository userAccountRepository;
    private final RoleInstanceRepository roleInstanceRepository;
    private final JDBCInformationProvider informationProvider;
    private final ApplicationContext applicationContext;

    @Override
    @Transactional
    public IUserAccount saveUserAccount(IUserAccount userAccount) {
        return userAccountRepository.save(cast(userAccount));
    }

    @Override
    @Transactional
    public void saveUserAccounts(List<IUserAccount> userAccounts) {
        userAccounts.forEach(this::saveUserAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public IUserAccount findUserAccountByLogin(String login) {
        return userAccountRepository.findByLogin(login);
    }

    @Override
    @Transactional
    public boolean changeUserPassword(String login, String oldPassword, String newPassword) {
        UserAccount userAccount = userAccountRepository.findByLogin(login);
        PasswordEncoder encoder = applicationContext.getBean(PasswordEncoder.class);
        if (encoder != null) {
            if (!encoder.matches(oldPassword, userAccount.getPassword())) {
                throw new IllegalStateException("Passed old password does not match to the user password");
            }
            userAccount.setPassword(encoder.encode(newPassword));
            this.saveUserAccount(userAccount);
            return true;
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IUserAccount> findAllUserAccounts(IUserAccount probe, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase();
        // Login
        if (!StringUtils.isNullOrEmpty(probe.getLogin())) {
            matcher = matcher.withMatcher("login", ExampleMatcher.GenericPropertyMatcher::contains);
        }
        // First name
        if (!StringUtils.isNullOrEmpty(probe.getFirstName())) {
            matcher = matcher.withMatcher("firstName", ExampleMatcher.GenericPropertyMatcher::contains);
        }
        // Last name
        if (!StringUtils.isNullOrEmpty(probe.getLastName())) {
            matcher = matcher.withMatcher("lastName", ExampleMatcher.GenericPropertyMatcher::contains);
        }
        // Blocked
        if (probe.getBlocked() != null) {
            matcher = matcher.withMatcher("blocked", ExampleMatcher.GenericPropertyMatcher::exact);
        }
        // Deleted
        if (probe.getDeleted() != null) {
            matcher = matcher.withMatcher("deleted", ExampleMatcher.GenericPropertyMatcher::exact);
        }

        Example<UserAccount> example = Example.of(cast(probe), matcher);
        Pageable pageRequest = preparePageRequest(pageable);
        return preparePageInstance(
                pageRequest,
                userAccountRepository.findAll(example, pageRequest)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<IUserAccount> findAllUserAccounts() {
        return new ArrayList<>(userAccountRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IUserAccount> findAllUserAccounts(Pageable pageable) {
        Pageable pageRequest = preparePageRequest(pageable);
        return preparePageInstance(
                pageRequest,
                userAccountRepository.findAll(pageRequest)
        );
    }

    @Override
    public long getUserAccountsCount() {
        return userAccountRepository.count();
    }

    @Override
    public boolean supportsUserManagement() {
        return true;
    }

    @Override
    public IUserAccount createSimpleUserAccountInstance() {
        return userAccountRepository.getInstance();
    }

    private Pageable preparePageRequest(Pageable pageable) {
        return new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.ASC, "login");
    }

    private Page<IUserAccount> preparePageInstance(Pageable pageable, Page<UserAccount> entityPage) {
        List<IUserAccount> content = new ArrayList<>(entityPage.getContent());
        return new PageImpl<>(
                content, pageable, entityPage.getTotalElements()
        );
    }

    private UserAccount cast(IUserAccount userAccount) {
        if (userAccount == null) {
            throw new IllegalArgumentException("userAccount parameter is null !");
        } else if (!(userAccount instanceof UserAccount)) {
            throw new IllegalArgumentException(
                    String.format("userAccount parameter is not an instance of %s ! [userAccount type: %s]",
                            UserAccount.class.getName(), userAccount.getClass().getName())
            );
        } else {
            return (UserAccount)userAccount;
        }
    }

    @Override
    public String getUserAccountProviderType() {
        return PROVIDER_TYPE;
    }

    @Override
    public String getUserAccountProviderSource() {
        return informationProvider.getUrl();
    }

    @Override
    public IRoleInstance createSimpleRoleInstance() {
        return roleInstanceRepository.getInstance();
    }

    @Override
    @Transactional
    public void detachRoleFromUsers(IBusinessRole businessRole) {
        BusinessRole businessRoleEntity = cast(businessRole);
        List<RoleInstance> roleInstances = roleInstanceRepository.findByBusinessRole(businessRoleEntity);
        roleInstanceRepository.deleteAll(roleInstances);
    }

    private BusinessRole cast(IBusinessRole businessRole) {
        if (businessRole == null) {
            throw new IllegalArgumentException("businessRole parameter is null !");
        } else if (!(businessRole instanceof BusinessRole)) {
            throw new IllegalArgumentException(
                    String.format("businessRole parameter is not an instance of %s ! [businessRole type: %s]",
                            BusinessRole.class.getName(), businessRole.getClass().getName())
            );
        } else {
            return (BusinessRole)businessRole;
        }
    }

}
