package pl.fhframework.core.security.provider.service;

import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;

import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2018-04-20)
 */
public interface BusinessRoleProvider {

    IBusinessRole createSimpleBusinessRoleInstance(String roleName);

    IBusinessRole saveBusinessRole(IBusinessRole businessRole);

    void saveBusinessRoles(List<IBusinessRole> roles);

    void deleteBusinessRole(IBusinessRole businessRole);

    IBusinessRole findBusinessRoleWithoutConversation(Long id);

    IBusinessRole findBusinessRoleByName(String roleName);

    List<IBusinessRole> findAllBusinessRoles();

    boolean supportsRoleManagement();

    String getBusinessRoleProviderType();

    String getBusinessRoleProviderSource();
}
