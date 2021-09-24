package pl.fhframework.model.security;

import lombok.Getter;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.util.StringUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class SystemUser {

    private String login;
    private String name;
    private String surname;
    private Principal principal;
    protected List<IBusinessRole> businessRoles;
    private boolean guest;

    public SystemUser(Principal principal) {
        this(principal, new ArrayList<>(), null, null);
    }

    public SystemUser(Principal principal, String name, String surname) {
        this(principal, new ArrayList<>(), name, surname);
    }

    public SystemUser(Principal principal, List<IBusinessRole> businessRoles) {
        this(principal, businessRoles, null, null);
    }

    public SystemUser(Principal principal, List<IBusinessRole> businessRoles, String name, String surname) {
        if (principal != null) {
            this.login = principal.getName();
            this.guest = false;
        } else {
            this.login = IBusinessRole.GUEST.toLowerCase();
            this.guest = true;
        }
        this.principal = principal;
        this.businessRoles = businessRoles;
        this.name = name != null ? name : this.login;
        this.surname = surname;
    }

    public SystemUser(Principal principal, String username, String name, String surname) {
        this.login = username;
        this.guest = false;
        this.principal = principal;
        this.businessRoles = new ArrayList<>();
        this.name = name != null ? name : this.login;
        this.surname = surname;
    }

    public boolean hasAnyRole(Collection<String> roleNames) {
        for (IBusinessRole userRole : businessRoles) {
            if (roleNames.contains(userRole.getRoleName())) {
                return true;
            }
        }
        return false;
    }

    /** Returns user full name */
    public String getFullName() {
        String fullName = name;
        if (!StringUtils.isNullOrEmpty(surname)) {
            fullName += " " + surname;
        }
        return fullName;
    }

}
