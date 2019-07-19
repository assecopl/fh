package pl.fhframework.integration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Tomasz Kozlowski (created on 20.05.2019)
 */
@Getter
@Setter
public class UsernameAndRolesAuthentication {

    /** User name */
    private String username;
    /** User roles separated by comma */
    private String roles;

}
