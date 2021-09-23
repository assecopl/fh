package pl.fhframework.core.security.provider.ad.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.provider.service.BusinessRoleProvider;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ADBusinessRoleProvider implements BusinessRoleProvider {
    private static final String PROVIDER_TYPE = "AD";

    @Override
    public IBusinessRole createSimpleBusinessRoleInstance(String roleName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBusinessRole saveBusinessRole(IBusinessRole businessRole) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveBusinessRoles(List<IBusinessRole> roles) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteBusinessRole(IBusinessRole businessRole) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBusinessRole findBusinessRoleWithoutConversation(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBusinessRole findBusinessRoleByName(String roleName) {
        return createSimpleBusinessRoleInstance(roleName);
    }


    @Override
    public List<IBusinessRole> findAllBusinessRoles() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supportsRoleManagement() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBusinessRoleProviderType() {
        return PROVIDER_TYPE;
    }

    @Override
    public String getBusinessRoleProviderSource() {
        throw new UnsupportedOperationException();
    }
}
