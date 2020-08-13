package pl.fhframework.core.security;

import java.util.Set;

/**
 * @author tomasz.kozlowski (created on 29.06.2018)
 */
public interface IDefaultUser {

    String getLogin();
    String getFirstName();
    String getLastName();
    String getPassword();
    Set<IDefaultRole> getRoles();

}
