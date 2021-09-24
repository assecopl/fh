package pl.fhframework.core.security.provider.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.security.provider.repository.PermissionRepository;
import pl.fhframework.fhPersistence.anotation.WithoutConversation;

import java.util.Collection;
import java.util.List;

/**
 * @author tomasz.kozlowski (created on 2018-04-20)
 */
@Service
@CacheConfig(cacheNames = "defined-permissions")
@RequiredArgsConstructor
public class PermissionProvider {

    private final PermissionRepository permissionRepository;

    public IPermission createSimplePermissionInstance(String businessRoleName, String functionName, String moduleUUID) {
        IPermission permission = permissionRepository.createInstance();
        permission.setBusinessRoleName(businessRoleName);
        permission.setFunctionName(functionName);
        permission.setModuleUUID(moduleUUID);
        permission.setDenial(false);
        return permission;
    }

    @Transactional
    @CacheEvict(key = "#permission.businessRoleName.toUpperCase()")
    public IPermission savePermission(IPermission permission) {
        return permissionRepository.save(permission);
    }

    @Transactional
    @CacheEvict(key = "#permission.businessRoleName.toUpperCase()")
    public void deletePermission(IPermission permission) {
        permissionRepository.delete(permission);
    }

    @Transactional
    @CacheEvict(key = "#businessRole.roleName.toUpperCase()")
    public void deletePermissionsForRole(IBusinessRole businessRole) {
        findPermissionsForRole(businessRole).forEach(
                this::deletePermission
        );
    }

    @Transactional(readOnly = true)
    @WithoutConversation
    @Cacheable(key = "#businessRole.roleName.toUpperCase()")
    public List<IPermission> findPermissionsForRole(IBusinessRole businessRole) {
        return permissionRepository.findForBusinessRole(businessRole.getRoleName());
    }

    @Transactional(readOnly = true)
    public List<IPermission> findByModuleUUIDAndFunctionNameIn(String moduleUUID, Collection<String> functions) {
        return permissionRepository.findForModuleAndFunction(moduleUUID, functions);
    }

    @Transactional
    @CacheEvict(key = "#oldRoleName.toUpperCase()")
    public void updatePermissionsForRole(String oldRoleName, String newRoleName) {
        List<IPermission> permissions = permissionRepository.findForBusinessRole(oldRoleName);
        permissions.forEach(
                permission -> permission.setBusinessRoleName(newRoleName)
        );
        permissionRepository.saveAll(permissions);
    }

}
