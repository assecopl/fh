package pl.fhframework.core.security.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.IBusinessRoleLoader;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.fhPersistence.anotation.WithoutConversation;

import java.util.Collection;

/**
 * Service which provides methods to get business roles.
 * This implementation requires bean of {@link pl.fhframework.core.security.ISecurityDataProvider} which provides security data.
 * @author tomasz.kozlowski (created on 2017-12-08)
 */
@Service
public class BusinessRoleLoader implements IBusinessRoleLoader {

    @Autowired
    private ISecurityDataProvider securityDataProvider;

    @Override
    @WithoutConversation
    public Collection<IBusinessRole> getBusinessRolesForUser(String userName) {
        return securityDataProvider.findBusinessRolesForUser(userName);
    }

    @Override
    @WithoutConversation
    public IBusinessRole getBusinessRoleByName(String roleName) {
        return securityDataProvider.findBusinessRoleByName(roleName);
    }

    @Override
    @WithoutConversation
    public Collection<IBusinessRole> getBusinessRolesForFunction(String moduleUUID, String systemFunction) {
        return securityDataProvider.findBusinessRolesForFunction(moduleUUID, systemFunction);
    }

    @Override
    @WithoutConversation
    public Collection<IPermission> getPermissionsForRole(IBusinessRole businessRole) {
        return securityDataProvider.findPermissionsForRole(businessRole);
    }

    @Override
    @WithoutConversation
    public IBusinessRole createSimpleBusinessRoleInstance(String roleName) {
        return securityDataProvider.createSimpleBusinessRoleInstance(roleName);
    }
}
