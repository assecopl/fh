package pl.fhframework.dp.commons.ad.kerberos.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IUserAccount;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 17/10/2020
 */
public class KerberosADUserDetailService implements UserDetailsService {

    private final KerberosADUserAccountProvider kerberosADUserAccountProvider;

    public KerberosADUserDetailService(KerberosADUserAccountProvider kerberosADUserAccountProvider) {
        this.kerberosADUserAccountProvider = kerberosADUserAccountProvider;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        IUserAccount userAccount = kerberosADUserAccountProvider.findUserAccountByLogin(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        } else {
            Set<String> allRoles = new HashSet();
            userAccount.getRoles().forEach((roleInstance) -> {
                this.getAllBusinessRoles(roleInstance.getBusinessRole(), allRoles);
            });
            return User.builder().username(username).password(userAccount.getPassword()).authorities((String[])allRoles.toArray(new String[0])).accountLocked(userAccount.getBlocked()).disabled(userAccount.getDeleted()).build();
        }
    }

    private void getAllBusinessRoles(IBusinessRole businessRole, Set<String> allRoles) {
        allRoles.add(businessRole.getRoleName());
        businessRole.getParentRoles().forEach((parentRole) -> {
            this.getAllBusinessRoles(parentRole, allRoles);
        });
    }
}
