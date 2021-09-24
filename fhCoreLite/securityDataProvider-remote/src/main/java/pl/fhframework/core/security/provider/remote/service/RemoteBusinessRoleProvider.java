package pl.fhframework.core.security.provider.remote.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.provider.remote.model.BusinessRole;
import pl.fhframework.core.security.provider.remote.repository.RemoteRepositoryManager;
import pl.fhframework.core.security.provider.service.BusinessRoleProvider;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomasz Kozlowski (created on 20.05.2019)
 */
@Service
@RequiredArgsConstructor
public class RemoteBusinessRoleProvider implements BusinessRoleProvider {

    private final RemoteRepositoryManager repositoryManager;

    @Override
    public IBusinessRole createSimpleBusinessRoleInstance(String roleName) {
        return new BusinessRole(roleName);
    }

    @Override
    public IBusinessRole findBusinessRoleByName(String roleName) {
        return createSimpleBusinessRoleInstance(roleName);
    }

    @Override
    public List<IBusinessRole> findAllBusinessRoles() {
        List<String> roles = repositoryManager.findAllBusinessRoles();
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        } else {
            return roles.stream()
                    .map(BusinessRole::new)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean supportsRoleManagement() {
        return false;
    }

    @Override
    public String getBusinessRoleProviderType() {
        return "Remote";
    }

    @Override
    public String getBusinessRoleProviderSource() {
        return repositoryManager.getRepositorySource();
    }

    // Unsupported operations ===========================================================================

    private static final String MESSAGE = "Remote Security Data Provider does not support this operation";

    @Override
    public IBusinessRole findBusinessRoleWithoutConversation(Long id) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public IBusinessRole saveBusinessRole(IBusinessRole businessRole) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void saveBusinessRoles(List<IBusinessRole> roles) {
        throw new UnsupportedOperationException(MESSAGE);
    }

    @Override
    public void deleteBusinessRole(IBusinessRole businessRole) {
        throw new UnsupportedOperationException(MESSAGE);
    }

}
