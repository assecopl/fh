package pl.fhframework.core.security.provider.jdbc.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.fhPersistence.core.BasePersistentObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author tomasz.kozlowski (created on 2017-11-22)
 */
@Getter
@Setter
@Entity
@Table(name = "SEC_BUSINESS_ROLES")
@SequenceGenerator(name = "SEQUENCE_GENERATOR", sequenceName = "SEC_BUSINESS_ROLES_ID_SEQ")
public class BusinessRole extends BasePersistentObject implements IBusinessRole {

    @Column(name = "ROLE_NAME", nullable = false, unique = true)
    private String roleName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ROOT_ROLE")
    private boolean rootRole;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(
            name = "SEC_ROLE_ROLE",
            joinColumns = { @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID") },
            inverseJoinColumns = { @JoinColumn(name = "CHILD_ID", referencedColumnName = "ID") }
    )
    private List<BusinessRole> subBusinessRoles = new ArrayList<>();

    @ManyToMany(mappedBy = "subBusinessRoles", cascade = CascadeType.REFRESH)
    private List<BusinessRole> parentBusinessRoles = new ArrayList<>();

    @Override
    public void addSubRole(IBusinessRole subRole) {
        if (subRole instanceof BusinessRole) {
            if (!subBusinessRoles.contains(subRole)) {
                subBusinessRoles.add((BusinessRole) subRole);
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("subRole parameter is not an instance of %s ! [subRole type: %s]",
                            BusinessRole.class.getName(), subRole.getClass().getName())
            );
        }
    }

    @Override
    public void removeSubRole(IBusinessRole subRole) {
        if (subRole instanceof BusinessRole) {
            subBusinessRoles.remove(subRole);
        } else {
            throw new IllegalArgumentException(
                    String.format("subRole parameter is not an instance of %s ! [subRole type: %s]",
                            BusinessRole.class.getName(), subRole.getClass().getName())
            );
        }
    }

    /**
     * Returns a view of sub business roles. Returned list instance is unmodifiable.
     *
     * NOTE: If there is a need of a sub roles list modification than use {@link BusinessRole#addSubRole(IBusinessRole)}
     * and {@link BusinessRole#removeSubRole(IBusinessRole)} methods.
     *
     * @return unmodifiable list of sub business roles.
     */
    @Override
    public List<IBusinessRole> getSubRoles() {
        return Collections.unmodifiableList(subBusinessRoles);
    }

    /**
     * Returns a view of parent business roles. Returned list instance is unmodifiable.
     *
     * @return unmodifiable list of parent business roles.
     */
    @Override
    public List<IBusinessRole> getParentRoles() {
        return Collections.unmodifiableList(parentBusinessRoles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessRole)) return false;
        BusinessRole that = (BusinessRole) o;
        if (getId() == null || that.getId() == null) {
            return Objects.equals(roleName, that.roleName);
        }
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : Objects.hashCode(roleName);
    }

}
