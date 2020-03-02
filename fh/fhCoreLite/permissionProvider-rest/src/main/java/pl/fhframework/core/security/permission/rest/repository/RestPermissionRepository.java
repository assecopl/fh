package pl.fhframework.core.security.permission.rest.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.security.permission.rest.config.PermissionsRestConfig;
import pl.fhframework.core.security.permission.rest.model.Permission;
import pl.fhframework.core.security.provider.repository.PermissionRepository;
import pl.fhframework.core.util.CollectionsUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tomasz Kozlowski (created on 17.10.2019)
 */
@Service
public class RestPermissionRepository implements PermissionRepository {

    private static final String PERMISSIONS = "permissions";
    private static final String PARAM_ROLE = "role";
    private static final String PARAM_ID = "id";
    private static final String PARAM_MODULE_UUID = "moduleUUID";
    private static final String PARAM_FUNCTIONS = "functions";

    @Value("${fhframework.security.permission.rest.rest-uri:}")
    private String restUri;

    @Override
    public IPermission createInstance() {
        return new Permission();
    }

    @Override
    public List<IPermission> findForBusinessRole(String businessRoleName) {
        // variables
        Map<String, String> variables = new HashMap<>();
        variables.put(PARAM_ROLE, businessRoleName);
        // url
        String url = UriComponentsBuilder.fromUriString(restUri)
                .pathSegment(PERMISSIONS)
                .pathSegment(String.format("{%s}", PARAM_ROLE))
                .buildAndExpand(variables)
                .toUriString();
        // get
        Permission[] result = PermissionsRestConfig.restTemplate.getForObject(url, Permission[].class);
        return CollectionsUtils.asNewList(result);
    }

    @Override
    public List<IPermission> findForModuleAndFunction(String moduleUUID, Collection<String> functions) {
        // url
        String url = UriComponentsBuilder.fromUriString(restUri)
                .pathSegment(PERMISSIONS)
                .queryParam(PARAM_MODULE_UUID, moduleUUID)
                .queryParam(PARAM_FUNCTIONS, String.join(",", functions))
                .toUriString();
        // get
        Permission[] result = PermissionsRestConfig.restTemplate.getForObject(url, Permission[].class);
        return CollectionsUtils.asNewList(result);
    }

    @Override
    public IPermission save(IPermission permission) {
        // url
        String url = UriComponentsBuilder.fromUriString(restUri)
                .pathSegment(PERMISSIONS)
                .toUriString();
        // post
        PermissionsRestConfig.restTemplate.postForObject(url, permission, Void.class);
        return permission;
    }

    @Override
    public void delete(IPermission permission) {
        // variables
        Map<String, Object> variables = new HashMap<>();
        variables.put(PARAM_ID, permission.getId());
        // url
        String url = UriComponentsBuilder.fromUriString(restUri)
                .pathSegment(PERMISSIONS)
                .pathSegment(String.format("{%s}", PARAM_ID))
                .buildAndExpand(variables)
                .toUriString();
        // delete
        PermissionsRestConfig.restTemplate.delete(url);
    }

}
