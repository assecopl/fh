package pl.fhframework.core.security.provider.repository;

import pl.fhframework.core.security.model.IPermission;

import java.util.Collection;
import java.util.List;

/**
 * An interface for permission repository.
 * @author tomasz.kozlowski (created on 2017-11-22)
 */
public interface PermissionRepository {

    /** Creates new instance of permission object */
    IPermission createInstance();

    /** Finds all permissions for given business role */
    List<IPermission> findForBusinessRole(String businessRoleName);

    /** Finds all permissions for given module UUID and functions */
    List<IPermission> findForModuleAndFunction(String moduleUUID, Collection<String> functions);

    /** Store permission instance */
    IPermission save(IPermission permission);

    /** Store collection of permissions */
    default void saveAll(List<IPermission> permissions) {
        permissions.forEach(this::save);
    }

    /** Deletes permission instance */
    void delete(IPermission permission);

}
