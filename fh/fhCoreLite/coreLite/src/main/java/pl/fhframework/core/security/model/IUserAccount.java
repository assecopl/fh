package pl.fhframework.core.security.model;

import java.util.List;

/**
 * Interface representing a user account.
 * @author tomasz.kozlowski (created on 2017-11-22)
 */
public interface IUserAccount {

    /** Gets a unique identifier of a user account */
    Long getId();

    /** Gets a user login */
    String getLogin();
    /** Sets a user login*/
    void setLogin(String login);

    /** Gets a user first name */
    String getFirstName();
    /** Sets a user first name */
    void setFirstName(String firstName);

    /** Gets a user last name */
    String getLastName();
    /** Sets a user last name */
    void setLastName(String lastName);

    /** Gets a user password (hashed) */
    String getPassword();
    /** Sets a user password (hashed) */
    void setPassword(String password);

    /** Gets a user email address */
    String getEmail();
    /** Sets a user email address */
    void setEmail(String email);

    /** Gets whether a user account is blocked */
    Boolean getBlocked();
    /** Sets whether a user account is blocked */
    void setBlocked(Boolean blocked);

    /** Gets information about reason of blocking user account */
    String getBlockingReason();
    /** Sets information about reason of blocking user account */
    void setBlockingReason(String blockingReason);

    /** Gets whether a user account is deleted */
    Boolean getDeleted();
    /** Sets whether a user account is deleted */
    void setDeleted(Boolean deleted);

    /** Gets collection of roles associated with a user account */
    List<IRoleInstance> getRoles();
    /** Adds new role instance */
    void addRole(IRoleInstance roleInstance);
    /** Remove role instance from user roles */
    void removeRole(IRoleInstance roleInstance);

}
