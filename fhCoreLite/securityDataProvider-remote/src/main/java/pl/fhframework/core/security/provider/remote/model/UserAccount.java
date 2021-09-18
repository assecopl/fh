package pl.fhframework.core.security.provider.remote.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.core.security.model.IUserAccount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tomasz Kozlowski (created on 20.05.2019)
 */
@Getter
@Setter
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
    private List<IRoleInstance> roleInstances = new ArrayList<>();

    @Override
    public List<IRoleInstance> getRoles() {
        return Collections.unmodifiableList(roleInstances);
    }

    @Override
    public void addRole(IRoleInstance roleInstance) {
        if (!roleInstances.contains(roleInstance)) {
            roleInstances.add(roleInstance);
        }
    }

    @Override
    public void removeRole(IRoleInstance roleInstance) {
        roleInstances.remove(roleInstance);
    }

}
