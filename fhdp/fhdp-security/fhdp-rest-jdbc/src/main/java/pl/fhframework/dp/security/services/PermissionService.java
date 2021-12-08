package pl.fhframework.dp.security.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.security.repositories.PermissionESRepository;
import pl.fhframework.dp.security.utils.SimpleIdGenerator;
import pl.fhframework.dp.transport.permissions.PermissionDto;
import pl.fhframework.dp.transport.roles.RoleDto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Tomasz Kozlowski (created on 27.01.2021)
 */
@Service
@AllArgsConstructor
public class PermissionService {

    private final PermissionESRepository permissionESRepository;

    public PermissionDto savePermission(PermissionDto permission) {
        if (permission.getId() == null) {
            permission .setId(SimpleIdGenerator.generateId());
        }
        return permissionESRepository.save(permission);
    }

    public List<PermissionDto> findPermissionsForRole(RoleDto role) {
        return findPermissionsForRole(role.getRoleName());
    }

    public List<PermissionDto> findPermissionsForRole(String roleName) {
        return permissionESRepository.findByRoleName(roleName);
    }

    public void updatePermissionsForRole(String oldRoleName, String newRoleName) {
        findPermissionsForRole(oldRoleName).forEach(permission -> {
            permission.setBusinessRoleName(newRoleName);
            permissionESRepository.save(permission);
        });
    }

    public void deletePermission(PermissionDto permission) {
        permissionESRepository.delete(permission);
    }

    public void deletePermission(Long id) {
        permissionESRepository.deleteById(id);
    }

    public void deletePermissionsForRole(RoleDto role) {
        deletePermissionsForRole(role.getRoleName());
    }

    public void deletePermissionsForRole(String roleName) {
        findPermissionsForRole(roleName).forEach(this::deletePermission);
    }

    public List<PermissionDto> findAllPermissions() {
        return StreamSupport.stream(permissionESRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

}
