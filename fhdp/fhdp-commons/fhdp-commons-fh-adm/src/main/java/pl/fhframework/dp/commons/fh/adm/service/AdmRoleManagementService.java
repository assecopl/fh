package pl.fhframework.dp.commons.fh.adm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fhframework.dp.commons.fh.utils.rest.facade.FacadeRestTemplateConfig;
import pl.fhframework.dp.transport.roles.RoleDto;

import java.util.Arrays;
import java.util.List;

/**
 * @author Tomasz Kozlowski (created on 04.03.2021)
 */
@Service
public class AdmRoleManagementService {

    private final String ROLES_PATH_SEGMENT = "roles";

    @Value("${fhframework.security.permission.rest.rest-uri}")
    private String restUri;

    public RoleDto saveRole(RoleDto roleDto) {
        String url = UriComponentsBuilder.fromUriString(restUri)
                .pathSegment(ROLES_PATH_SEGMENT)
                .toUriString();

        return FacadeRestTemplateConfig.restTemplate.postForObject(url, roleDto, RoleDto.class);
    }

    public List<RoleDto> findAllRoles() {
        String url = UriComponentsBuilder.fromUriString(restUri)
                .pathSegment(ROLES_PATH_SEGMENT)
                .pathSegment("all")
                .toUriString();

        return Arrays.asList(FacadeRestTemplateConfig.restTemplate.getForObject(url, RoleDto[].class));
    }

    public RoleDto findRoleByName(String roleName) {
        String url = UriComponentsBuilder.fromUriString(restUri)
                .pathSegment(ROLES_PATH_SEGMENT)
                .pathSegment("{roleName}").buildAndExpand(roleName)
                .toUriString();

        return FacadeRestTemplateConfig.restTemplate.getForObject(url, RoleDto.class);
    }

    public void deleteRole(RoleDto roleDto) {
        String url = UriComponentsBuilder.fromUriString(restUri)
                .pathSegment(ROLES_PATH_SEGMENT)
                .pathSegment("{id}").buildAndExpand(roleDto.getId())
                .toUriString();

        FacadeRestTemplateConfig.restTemplate.delete(url);
    }

}

