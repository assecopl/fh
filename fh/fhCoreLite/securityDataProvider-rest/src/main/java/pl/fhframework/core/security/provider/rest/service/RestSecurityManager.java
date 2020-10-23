package pl.fhframework.core.security.provider.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import pl.fhframework.core.security.CoreSecurityManager;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.model.security.SystemUser;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Set;

@Service
@Transactional
@Primary
public class RestSecurityManager extends CoreSecurityManager {
    public RestSecurityManager(@Autowired ISecurityDataProvider securityDataProvider) {
        super(securityDataProvider);
    }

    @Override
    public SystemUser buildSystemUser(Principal principal) {
        String firstName = null;
        String lastName = null;
        String userLogin = null;
        if (principal != null) {
            IUserAccount userAccount = securityDataProvider.findUserAccountByLogin(principal.getName());
            if (userAccount != null) {
                firstName = userAccount.getFirstName();
                lastName = userAccount.getLastName();
                userLogin = userAccount.getLogin();
            }
        }

        SystemUser systemUser = new SystemUser(principal, firstName, lastName);

        // for liferay
        if (!StringUtils.isEmpty(userLogin)) {
            Field login = ReflectionUtils.findField(SystemUser.class, "login");
            ReflectionUtils.makeAccessible(login);
            ReflectionUtils.setField(login, systemUser, userLogin);
        }

        Set<IBusinessRole> userBusinessRoles = getUserBusinessRoles(systemUser);
        systemUser.getBusinessRoles().addAll(userBusinessRoles);

        return systemUser;
    }
}
