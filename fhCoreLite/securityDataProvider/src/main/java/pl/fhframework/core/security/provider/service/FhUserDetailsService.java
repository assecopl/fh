package pl.fhframework.core.security.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IUserAccount;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Tomasz Kozlowski (created on 17.05.2019)
 */
@Service
@Transactional
public class FhUserDetailsService implements UserDetailsService {

    private UserAccountProvider userAccountProvider;

    @Autowired
    public void setUserAccountProvider(UserAccountProvider userAccountProvider) {
        this.userAccountProvider = userAccountProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        IUserAccount userAccount = userAccountProvider.findUserAccountByLogin(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        if (userAccount.getBlocked()) {
            throw new LockedException("User " + username + " is blocked");
        }
        if (userAccount.getDeleted()) {
            throw new AccountExpiredException("Account for user " + username + " is deleted");
        }

        Set<String> allRoles = new HashSet<>();
        userAccount.getRoles().forEach(
                roleInstance -> getAllBusinessRoles(roleInstance.getBusinessRole(), allRoles)
        );

        return User.builder()
                .username(username)
                .password(userAccount.getPassword())
                .authorities(allRoles.toArray(new String[]{}))
                .accountLocked(userAccount.getBlocked())
                .disabled(userAccount.getDeleted())
                .build();
    }

    /**
     * Transforms hierarchy of business roles into a flat collection of roles names.
     * As a result method returns collection (param <code>businessRole</code>)containing given business role and its all parent roles.
     * NOTICE: Recursive call.
     *
     * @param businessRole a business role for which the hierarchy is specifying.
     * @param allRoles result collection with roles names
     */
    private void getAllBusinessRoles(IBusinessRole businessRole, Set<String> allRoles) {
        allRoles.add(businessRole.getRoleName());
        businessRole.getParentRoles().forEach(
                parentRole -> getAllBusinessRoles(parentRole, allRoles)
        );
    }

}
