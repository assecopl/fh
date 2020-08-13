package pl.fhframework.core.security.model;

import java.util.List;

/** Interface representing a business role. */
public interface IBusinessRole {

    String GUEST = "Guest", USER = "User";

    String NONE = "-- NONE --";

    /** Gets a unique role identifier */
    Long getId();

    /** Gets name for the business role */
    String getRoleName();
    /** Sets name for the business role */
    void setRoleName(String roleName);

    /** Gets description of the role */
    String getDescription();
    /** Sets description of the role */
    void setDescription(String description);

    /** Gets whether role is a root role */
    boolean isRootRole();
    /** Sets whether role is a root role */
    void setRootRole(boolean rootRole);

    /** Adds new sub role */
    void addSubRole(IBusinessRole subRole);
    /** Removes a sub role */
    void removeSubRole(IBusinessRole subRole);
    /** Gets collection of sub roles */
    List<IBusinessRole> getSubRoles();

    /** Gets collection of parent roles */
    List<IBusinessRole> getParentRoles();

}
