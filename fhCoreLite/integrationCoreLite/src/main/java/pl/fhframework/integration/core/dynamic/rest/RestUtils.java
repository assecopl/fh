package pl.fhframework.integration.core.dynamic.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.integration.BasicAuthentication;
import pl.fhframework.integration.IRestUtils;
import pl.fhframework.integration.RestResource;
import pl.fhframework.integration.UsernameAndRolesAuthentication;
import pl.fhframework.integration.core.endpoints.model.Endpoint;
import pl.fhframework.integration.core.endpoints.model.EndpointSecurityTypeEnum;
import pl.fhframework.integration.core.endpoints.service.EndpointRepository;
import pl.fhframework.model.security.SystemUser;

import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2018-04-12.
 */
@Component
public class RestUtils implements IRestUtils {
    public static final String NAME = "__restUtils";

    @Autowired
    private EndpointRepository endpointRepository;

    public RestResource buildRestResource(String endpointName, String endpointUrl, String resourceUri) {
        RestResource restResource = new RestResource();
        if (!StringUtils.isNullOrEmpty(endpointName)) {
            Endpoint endpoint = endpointRepository.findOneByName(endpointName);
            if (endpoint != null) {
                restResource.setUrl(endpoint.getUrl() + resourceUri);
                if (endpoint.getSecurityType() == EndpointSecurityTypeEnum.BasicAuthentication) {
                    restResource.setBasicAuthentication(new BasicAuthentication());
                    restResource.getBasicAuthentication().setUsername(endpoint.getUsername());
                    restResource.getBasicAuthentication().setPassword(endpoint.getPassword());
                } else if (endpoint.getSecurityType() == EndpointSecurityTypeEnum.UsernameAndRolesAuthentication) {
                    restResource.setUsernameAndRolesAuthentication(createUsernameAndRolesAuthentication());
                }

                return restResource;
            }
        }

        restResource.setUrl(endpointUrl + resourceUri);

        return restResource;
    }

    private UsernameAndRolesAuthentication createUsernameAndRolesAuthentication() {
        UsernameAndRolesAuthentication authentication = new UsernameAndRolesAuthentication();
        UserSession userSession = SessionManager.getUserSession();
        if (userSession != null) {
            SystemUser systemUser = userSession.getSystemUser();
            if (systemUser != null) {
                authentication.setUsername(systemUser.getLogin());
                String roles =  systemUser.getBusinessRoles().stream()
                        .map(IBusinessRole::getRoleName)
                        .collect(Collectors.joining(","));
                authentication.setRoles(roles);
            }
        }

        return authentication;
    }

}
