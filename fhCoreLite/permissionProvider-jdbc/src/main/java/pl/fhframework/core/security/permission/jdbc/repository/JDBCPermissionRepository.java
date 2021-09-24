package pl.fhframework.core.security.permission.jdbc.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.security.permission.jdbc.model.Permission;
import pl.fhframework.core.security.provider.repository.PermissionRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tomasz Kozlowski (created on 16.10.2019)
 */
@Service
@RequiredArgsConstructor
public class JDBCPermissionRepository implements PermissionRepository {

    private final JpaPermissionRepository permissionRepository;

    @Override
    public IPermission createInstance() {
        return new Permission();
    }

    @Override
    public List<IPermission> findForBusinessRole(String businessRoleName) {
        return new ArrayList<>(permissionRepository.findByBusinessRoleNameIgnoreCase(businessRoleName));
    }

    @Override
    public List<IPermission> findForModuleAndFunction(String moduleUUID, Collection<String> functions) {
        return new ArrayList<>(permissionRepository.findByModuleUUIDAndFunctionNameIn(moduleUUID, functions));
    }

    @Override
    public IPermission save(IPermission permission) {
        return permissionRepository.save(cast(permission));
    }

    @Override
    public void delete(IPermission permission) {
        permissionRepository.delete(cast(permission));
    }

    private Permission cast(IPermission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission parameter is null !");
        } else if (!(permission instanceof Permission)) {
            throw new IllegalArgumentException(
                    String.format("permission parameter is not an instance of %s ! [permission type: %s]",
                            Permission.class.getName(), permission.getClass().getName())
            );
        } else {
            return (Permission) permission;
        }
    }

}
