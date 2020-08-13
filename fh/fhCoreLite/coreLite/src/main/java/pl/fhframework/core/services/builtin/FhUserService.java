package pl.fhframework.core.services.builtin;

import pl.fhframework.core.CoreSystemFunction;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.services.FhService;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.SessionManager;
import pl.fhframework.model.security.SystemUser;

import java.util.Arrays;

/**
 * Build-in service gives access to user information
 */
@FhService(groupName = "user", categories = "security")
@SystemFunction(CoreSystemFunction.CORE_SERVICES_USER)
public class FhUserService {

    public boolean hasRole(@Parameter(name = "role") String role) {
        SystemUser user = SessionManager.getSystemUser();

        if (user == null) {
            return false;
        }

        return user.hasAnyRole(Arrays.asList(role));
    }
}
