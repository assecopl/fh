package pl.fhframework.core.security;

import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.model.security.SystemUser;

import java.security.Principal;
import java.util.Set;

/**
 * @author Tomasz.Kozlowski (created on 04.10.2018)
 */
public interface SecurityManager {

    /**
     * Creates a system user instance.
     * @param principal an instance of principal
     * @return created system user instance.
     */
    SystemUser buildSystemUser(Principal principal);

    /**
     * Builds an effective (flat) collection of business roles for given user.
     * @param systemUser a system user instance.
     * @return an effective (flat) collection of business roles for given user.
     */
    Set<IBusinessRole> getUserBusinessRoles(SystemUser systemUser);

}
