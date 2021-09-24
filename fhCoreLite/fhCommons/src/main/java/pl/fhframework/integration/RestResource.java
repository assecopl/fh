package pl.fhframework.integration;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by pawel.ruta on 2018-04-12.
 */
@Getter
@Setter
public class RestResource {
    private String url;

    private BasicAuthentication basicAuthentication;

    private UsernameAndRolesAuthentication usernameAndRolesAuthentication;

}
