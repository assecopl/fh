package pl.fhframework.core.security.model;

import java.time.LocalDate;

/**
 * Interface representing a role instance.
 * @author tomasz.kozlowski (created on 2017-11-24)
 */
public interface IRoleInstance {

    /** Gets a role instance identifier */
    Long getId();

    /** Gets assignment time */
    LocalDate getAssignmentTime();
    /** Sets assignment time */
    void setAssignmentTime(LocalDate assignmentTime);

    /** Gets role instance activation time */
    LocalDate getValidFrom();
    /** Sets role instance activation time */
    void setValidFrom(LocalDate validFrom);

    /** Gets role instance expiry time */
    LocalDate getValidTo();
    /** Sets role instance expiry time */
    void setValidTo(LocalDate validTo);

    /** Gets business role associated with role instance */
    IBusinessRole getBusinessRole();
    /** Sets business role associated with role instance */
    void setBusinessRole(IBusinessRole businessRole);

}
