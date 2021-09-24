package pl.fhframework.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.model.security.SystemUser;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class CoreSecurityManager implements SecurityManager {

    protected final ISecurityDataProvider securityDataProvider;

    @Override
    public SystemUser buildSystemUser(Principal principal) {
        String firstName = null;
        String lastName = null;
        if (principal != null) {
            IUserAccount userAccount = securityDataProvider.findUserAccountByLogin(principal.getName());
            if (userAccount != null) {
                firstName = userAccount.getFirstName();
                lastName = userAccount.getLastName();
            }
        }

        SystemUser systemUser = new SystemUser(principal, firstName, lastName);
        Set<IBusinessRole> userBusinessRoles = getUserBusinessRoles(systemUser);
        systemUser.getBusinessRoles().addAll(userBusinessRoles);
        return systemUser;
    }

    @Override
    public Set<IBusinessRole> getUserBusinessRoles(SystemUser systemUser) {
        if (systemUser.isGuest()) { // Guest
            IBusinessRole businessRole = securityDataProvider.findBusinessRoleByName(IBusinessRole.GUEST);
            if (businessRole == null) {
                businessRole = securityDataProvider.createSimpleBusinessRoleInstance(IBusinessRole.GUEST);
            }
            return Collections.singleton(businessRole);
        } else {
            // builds flat collection of business roles assigned to the user
            Set<IBusinessRole> allRoles = new HashSet<>();
            if (systemUser.getPrincipal() instanceof AbstractAuthenticationToken) {
                Collection<GrantedAuthority> authorities = ((AbstractAuthenticationToken) systemUser.getPrincipal()).getAuthorities();
                for(GrantedAuthority grantedAuthority : authorities) {
                    IBusinessRole businessRole = securityDataProvider.findBusinessRoleByName(grantedAuthority.getAuthority());
                    if (businessRole == null) {
                        businessRole = securityDataProvider.createSimpleBusinessRoleInstance(grantedAuthority.getAuthority());
                    }
                    getAllBusinessRoles(businessRole, allRoles);
                }
            } else {
                securityDataProvider.findBusinessRolesForUser(systemUser.getLogin())
                        .forEach(role -> getAllBusinessRoles(role, allRoles));
            }
            return allRoles;
        }
    }

    /**
     * Transforms hierarchy of business roles into a flat collection.
     * As a result method returns collection (param <code>businessRole</code>)containing given business role and its all parent roles.
     * NOTICE: Recursive call.
     *
     * @param businessRole a business role for which the hierarchy is specifying.
     * @param allRoles result collection
     */
    protected void getAllBusinessRoles(IBusinessRole businessRole, Set<IBusinessRole> allRoles) {
        allRoles.add(businessRole);
        businessRole.getParentRoles().forEach(
                parentRole -> getAllBusinessRoles(parentRole, allRoles)
        );
    }

}
