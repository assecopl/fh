package pl.fhframework.core.security.provider.jdbc.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;
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
@Table(name = "SEC_USER_ACCOUNTS")
@SequenceGenerator(name = "SEQUENCE_GENERATOR", sequenceName = "SEC_USER_ACCOUNTS_ID_SEQ")
public class UserAccount extends BasePersistentObject implements IUserAccount {

    @Column(name = "LOGIN", nullable = false, unique = true)
    private String login;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "BLOCKED")
    private Boolean blocked = Boolean.FALSE;

    @Column(name = "BLOCKING_REASON")
    private String blockingReason;

    @Column(name = "DELETED")
    private Boolean deleted = Boolean.FALSE;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name="USER_ACCOUNT_ID", referencedColumnName = "ID")
    private List<RoleInstance> roleInstances = new ArrayList<>();

    @Override
    public void addRole(IRoleInstance roleInstance) {
        if (roleInstance instanceof RoleInstance) {
            if (!roleInstances.contains(roleInstance)) {
                roleInstances.add((RoleInstance) roleInstance);
            }
        } else {
            throw new IllegalArgumentException(
                    String.format("roleInstance parameter is not an instance of %s ! [roleInstance type: %s]",
                            RoleInstance.class.getName(), roleInstance.getClass().getName())
            );
        }
    }

    @Override
    public void removeRole(IRoleInstance roleInstance) {
        if (roleInstance instanceof RoleInstance) {
            roleInstances.remove(roleInstance);
        } else {
            throw new IllegalArgumentException(
                    String.format("roleInstance parameter is not an instance of %s ! [roleInstance type: %s]",
                            RoleInstance.class.getName(), roleInstance.getClass().getName())
            );
        }
    }

    /**
     * Returns a view of role instances associated with user account. Returned list instance is unmodifiable.
     *
     * NOTE: If there is a need of a roles list modification than use {@link UserAccount#addRole(IRoleInstance)}
     * and {@link UserAccount#removeRole(IRoleInstance)} methods.
     *
     * @return a view of role instances associated with user account.
     */
    @Override
    public List<IRoleInstance> getRoles() {
        return Collections.unmodifiableList(roleInstances);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount)) return false;
        UserAccount that = (UserAccount) o;
        if (getId() == null || that.getId() == null) {
            return Objects.equals(login, that.login);
        }
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : Objects.hashCode(login);
    }

}
