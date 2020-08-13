package pl.fhframework.core.security.provider.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
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
        IUserAccount userAccount = securityDataProvider.findUserAccountByLogin(principal.getName());
        String firstName = userAccount.getFirstName();
        String lastName = userAccount.getLastName();

        SystemUser systemUser = new SystemUser(principal, firstName, lastName);
        // for liferay
        Field login = ReflectionUtils.findField(SystemUser.class, "login");
        ReflectionUtils.makeAccessible(login);
        ReflectionUtils.setField(login, systemUser, userAccount.getLogin());

        Set<IBusinessRole> userBusinessRoles = getUserBusinessRoles(systemUser);
        systemUser.getBusinessRoles().addAll(userBusinessRoles);

        return systemUser;
    }
}
