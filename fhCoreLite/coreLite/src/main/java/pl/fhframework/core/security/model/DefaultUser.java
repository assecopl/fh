package pl.fhframework.core.security.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.fhframework.core.security.IDefaultRole;
import pl.fhframework.core.security.IDefaultUser;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default user model use for initialize system by custom users if there are no users in repository.
 * @author tomasz.kozlowski (created on 2017-12-13)
 */
@Getter
@EqualsAndHashCode(of = "login")
public class DefaultUser implements IDefaultUser {

    private String login;
    private String firstName;
    private String lastName;
    private String password;
    private Set<IDefaultRole> roles;

    public DefaultUser(String login, String password, IDefaultRole... roles) {
        this(login, null, null, password, roles);
    }

    public DefaultUser(String login, String firstName, String lastName, String password, IDefaultRole... roles) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roles = Arrays.stream(roles).collect(Collectors.toSet());
    }

}
