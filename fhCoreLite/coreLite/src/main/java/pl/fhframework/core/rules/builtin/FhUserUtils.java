package pl.fhframework.core.rules.builtin;

import lombok.RequiredArgsConstructor;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.SessionManager;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.model.IUserAccount;
import pl.fhframework.model.security.SystemUser;

import java.util.Collections;

/**
 * Build-in rules which give access to user information
 */
@BusinessRule(categories = {"security", "user"})
@RequiredArgsConstructor
public class FhUserUtils {

    private final ISecurityDataProvider securityDataProvider;

    public boolean userHasRole(String role) {
        SystemUser user = SessionManager.getSystemUser();
        if (user == null) {
            return false;
        }

        return user.hasAnyRole(Collections.singletonList(role));
    }

    public String userLogin() {
        return SessionManager.getUserLogin();
    }

    public String userFirstName() {
        SystemUser user = SessionManager.getSystemUser();
        return user != null ? user.getName() : "";
    }

    public String userFirstName(String login) {
        IUserAccount userAccount = securityDataProvider.findUserAccountByLogin(login);
        return userAccount != null ? userAccount.getFirstName() : "";
    }

    public String userLastName() {
        SystemUser user = SessionManager.getSystemUser();
        return user != null ? user.getSurname() : "";
    }

    public String userLastName(String login) {
        IUserAccount userAccount = securityDataProvider.findUserAccountByLogin(login);
        return userAccount != null ? userAccount.getLastName() : "";
    }

}
