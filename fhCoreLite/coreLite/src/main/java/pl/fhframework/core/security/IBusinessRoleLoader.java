package pl.fhframework.core.security;

import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IPermission;

import java.util.Collection;

/** Interface provides methods to get business roles and permissions. */
public interface IBusinessRoleLoader {

    /**
     * Returns business roles for given user.
     * @param userName - some login or other unique value, that user can be determined by
     * @return collection of business roles
     */
    Collection<IBusinessRole> getBusinessRolesForUser(String userName);

    /**
     * Returns role by its name.
     * @param roleName role name
     * @return role object or null
     */
    IBusinessRole getBusinessRoleByName(String roleName);

    /**
     * Returns which business roles have permission to given system function.
     * @param moduleUUID module UUID
     * @param systemFunction system function name
     * @return collection of business roles
     */
    Collection<IBusinessRole> getBusinessRolesForFunction(String moduleUUID, String systemFunction);

    /**
     * Returns collection of permissions for given business role.
     * @param businessRole business role
     * @return collection of permissions
     */
    Collection<IPermission> getPermissionsForRole(IBusinessRole businessRole);

    /**
     * Creates a business role instance with specific name.
     * @param roleName name of a new business role
     * @return object of a new business role
     */
    IBusinessRole createSimpleBusinessRoleInstance(String roleName);

}
