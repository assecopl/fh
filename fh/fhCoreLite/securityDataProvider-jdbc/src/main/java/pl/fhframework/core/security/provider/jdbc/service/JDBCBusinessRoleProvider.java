package pl.fhframework.core.security.provider.jdbc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.provider.jdbc.model.BusinessRole;
import pl.fhframework.core.security.provider.jdbc.repository.BusinessRoleRepository;
import pl.fhframework.core.security.provider.service.BusinessRoleProvider;
import pl.fhframework.fhPersistence.anotation.WithoutConversation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2018-04-20)
 */
@Service
public class JDBCBusinessRoleProvider implements BusinessRoleProvider {

    private static final String PROVIDER_TYPE = "JDBC";

    @Autowired
    private BusinessRoleRepository businessRoleRepository;

    @Autowired
    private JDBCInformationProvider informationProvider;

    @Override
    public IBusinessRole createSimpleBusinessRoleInstance(String roleName) {
        BusinessRole businessRole = businessRoleRepository.getInstance();
        businessRole.setRoleName(roleName);
        businessRole.setRootRole(true);
        return businessRole;
    }

    @Override
    @Transactional
    public IBusinessRole saveBusinessRole(IBusinessRole businessRole) {
        return businessRoleRepository.save(cast(businessRole));
    }

    @Override
    @Transactional
    public void saveBusinessRoles(List<IBusinessRole> roles) {
        roles.forEach(this::saveBusinessRole);
    }

    @Override
    @Transactional
    public void deleteBusinessRole(IBusinessRole businessRole) {
        BusinessRole businessRoleEntity = cast(businessRole);
        // delete role to sub role relations
        businessRoleRepository.findBySubBusinessRoles_Id(businessRoleEntity.getId())
                .forEach(role -> role.removeSubRole(businessRoleEntity));
        // change sub roles into top roles
        businessRoleEntity.getSubRoles()
                .forEach(role ->  {
                    role.setRootRole(true);
                    businessRoleRepository.save(cast(role));
                });
        // delete business role
        businessRoleRepository.delete(businessRoleEntity);
    }

    @Override
    @Transactional(readOnly = true)
    @WithoutConversation
    public IBusinessRole findBusinessRoleWithoutConversation(Long id) {
        return businessRoleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public IBusinessRole findBusinessRoleByName(String roleName) {
        return businessRoleRepository.findByRoleNameIgnoreCase(roleName);
    }

    @Override
    @Transactional
    public List<IBusinessRole> findAllBusinessRoles() {
        return new ArrayList<>(businessRoleRepository.findAll());
    }

    @Override
    public boolean supportsRoleManagement() {
        return true;
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

    @Override
    public String getBusinessRoleProviderType() {
        return PROVIDER_TYPE;
    }

    @Override
    public String getBusinessRoleProviderSource() {
        return informationProvider.getUrl();
    }
}
