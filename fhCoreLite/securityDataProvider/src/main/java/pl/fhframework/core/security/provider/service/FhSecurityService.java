package pl.fhframework.core.security.provider.service;

import lombok.RequiredArgsConstructor;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.core.services.FhService;
import pl.fhframework.SessionManager;

import java.util.List;

/**
 * @author Tomasz.Kozlowski (created on 12.02.2019)
 */
@FhService(groupName = "security", categories = "security")
@RequiredArgsConstructor
public class FhSecurityService {

    private final ISecurityDataProvider securityDataProvider;

    /** Method finds and returns a user account object for active user. */
    public IUserAccount getUserAccount() {
        return securityDataProvider.findUserAccountByLogin(SessionManager.getUserLogin());
    }

    /** Method finds and returns collection of business roles for active user. */
    public List<IBusinessRole> getUserBusinessRoles() {
        return securityDataProvider.findBusinessRolesForUser(SessionManager.getUserLogin());
    }

    /**
     * Method changes password for active user.
     * @param oldPassword NOT encrypted old user password.
     * @param newPassword NOT encrypted new user password.
     * @return <code>true</code> - if password changing operation was ended successfully,
     *         <code>false</code> - otherwise.
     */
    public boolean changeUserPassword(String oldPassword, String newPassword) {
        try {
            return securityDataProvider.changeUserPassword(SessionManager.getUserLogin(), oldPassword, newPassword);
        } catch (Exception e) {
            FhLogger.error(e);
            return false;
        }
    }

}
