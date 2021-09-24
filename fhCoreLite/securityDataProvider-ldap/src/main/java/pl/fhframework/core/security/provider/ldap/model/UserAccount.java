package pl.fhframework.core.security.provider.ldap.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Tomasz Kozlowski (created on 14.10.2019)
 */
@Setter
@Getter
public class UserAccount implements IUserAccount {

    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private Boolean blocked = Boolean.FALSE;
    private String blockingReason;
    private Boolean deleted = Boolean.FALSE;

    @Override
    public List<IRoleInstance> getRoles() {
        return Collections.emptyList();
    }

    @Override
    public void addRole(IRoleInstance roleInstance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeRole(IRoleInstance roleInstance) {
        throw new UnsupportedOperationException();
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
