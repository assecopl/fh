package pl.fhframework.core.security.provider.rest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fhframework.core.security.provider.rest.config.RestTemplateConfig;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.provider.remote.model.UserInfo;
import pl.fhframework.core.security.provider.remote.repository.RemoteSecurityDataRepository;
import pl.fhframework.core.util.CollectionsUtils;

import java.util.HashMap;
import java.util.List;

@Service
public class RestSecurityDataRepository implements RemoteSecurityDataRepository {

    private static final String USER = "user-light";
    private static final String ALL_USERS = "users";
    private static final String ALL_ROLES = "roles";

    @Value("${fhframework.security.provider.rest.idpUri:}")
    private String idpUri;
    @Value("${fhframework.security.provider.rest.order:1}")
    private int order;

    // U S E R S
    // -------------------------------------------------------------

    /** Finds information about given user */
    @Override
    public UserInfo findUserByName(String username) {
        FhLogger.info(this.getClass(), String.format("Logging in '%s'", username));
        return RestTemplateConfig.restTemplate.getForObject(
                UriComponentsBuilder.fromUriString(idpUri).pathSegment(USER).pathSegment("{username}").buildAndExpand(new HashMap<String, String>() {
                    {
                        put("username", username);
                    }
                }).toUriString(),
                UserInfo.class);
    }

    /** Returns all users */
    @Override
    public List<UserInfo> findAllUsers() {
        return CollectionsUtils.asNewList(RestTemplateConfig.restTemplate.getForObject(
                UriComponentsBuilder.fromUriString(idpUri).pathSegment(ALL_USERS).build().toUriString(),
                UserInfo[].class, new HashMap<String, String>()));
    }

    // R O L E S
    // -------------------------------------------------------------

    /** Returns all business */
    @Override
    public List<String> findAllBusinessRoles() {
        return CollectionsUtils.asNewList(RestTemplateConfig.restTemplate.getForObject(
                UriComponentsBuilder.fromUriString(idpUri).pathSegment(ALL_ROLES).build().toUriString(),
                String[].class, new HashMap<String, String>()));
    }

    // -------------------------------------------------------------

    @Override
    public String getRepositorySource() {
        return "REST Security Data Repository";
    }

    @Override
    public int getOrder() {
        return order;
    }

}

