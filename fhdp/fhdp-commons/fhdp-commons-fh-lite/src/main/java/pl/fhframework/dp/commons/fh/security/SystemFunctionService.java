package pl.fhframework.dp.commons.fh.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.Session;
import pl.fhframework.SessionManager;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.security.SystemUser;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class SystemFunctionService {

    @Autowired
    AuthorizationManager authManager;

    //Sprawdza czy użytkownik ma podaną funkcję systemową dla modułu o podanym UUID
    public boolean hasSystemFunction(String systemFunction, String moduleUUID) {
        if (systemFunction == null || moduleUUID == null)
            return false;

        List<IBusinessRole> roles = Optional.ofNullable(SessionManager.getUserSession())
                .map(Session::getSystemUser)
                .map(SystemUser::getBusinessRoles)
                .orElse(null);

        if (roles == null)
            return false;
        else
            return authManager.hasFunction(roles, systemFunction, moduleUUID);
    }

    //wygodnie zwraca enum dostępności gdy użytkownik ma / nie ma funkcji
    public AccessibilityEnum accessBasedOnSystemFunction(String systemFunction, String moduleUUID, AccessibilityEnum whenHasFunction, AccessibilityEnum whenHasNotFunction) {
        if (hasSystemFunction(systemFunction, moduleUUID))
            return whenHasFunction;
        else
            return whenHasNotFunction;
    }

    //wygodnie pozwala konfigurować  enum dostępności na podstawie uprawnień  gdy użytkownik ma / nie ma funkcji i lambdy
    public AccessibilityEnum accessBasedOnSystemFunction(String systemFunction, String moduleUUID, Supplier<AccessibilityEnum> whenHasFunction, Supplier<AccessibilityEnum> whenHasNotFunction) {
        if (hasSystemFunction(systemFunction, moduleUUID))
            return whenHasFunction.get();
        else
            return whenHasNotFunction.get();
    }

}
