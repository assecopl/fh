package pl.fhframework.integration.core.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pl.fhframework.core.util.JacksonUtils;
import pl.fhframework.integration.IRestUtils;
import pl.fhframework.integration.RestResource;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pawel.ruta on 2018-10-17.
 */
@Component
public class RestClientProxy {
    @Autowired
    private IRestUtils restUtils;

    @Autowired
    private JacksonUtils jacksonUtils;

    @Autowired
    private RestTemplate restTemplate;

    public <T> T call(RestDescriptor restDescriptor) {
        RestResource restResource = restUtils.buildRestResource(restDescriptor.getEndpointOrUrl(), restDescriptor.getEndpointOrUrl(), restDescriptor.getUri());
        String restUrl = restResource.getUrl();
        HttpHeaders restHeaders = new HttpHeaders();
        restHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        restHeaders.setContentType(MediaType.APPLICATION_JSON);
        if (restResource.getBasicAuthentication() != null) {
            String auth = restResource.getBasicAuthentication().getUsername() + ":" + restResource.getBasicAuthentication().getPassword();
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
            restHeaders.set("Authorization", "Basic " + new String(encodedAuth));
        }

        if (restResource.getUsernameAndRolesAuthentication() != null) {
            restHeaders.add("userName", restResource.getUsernameAndRolesAuthentication().getUsername());
            restHeaders.add("userRoles", restResource.getUsernameAndRolesAuthentication().getRoles());
        }

        restDescriptor.getHeaderParams().forEach((name, value) -> restHeaders.set(name, jacksonUtils.writeValueAsText(value)));

        HttpEntity<Object> restEntity;

        if (restDescriptor.getBody() != null) {
            restEntity = new HttpEntity<>(restDescriptor.getBody(), restHeaders);
        } else {
            restEntity = new HttpEntity<>(restHeaders);
        }
        Map<String, Object> restUriParams = new HashMap<>();
        restDescriptor.getUriParams().forEach((name, value) ->
            restUriParams.put(name, jacksonUtils.writeValueAsText(value)));


        UriComponentsBuilder restBuilder = UriComponentsBuilder.fromUriString(restUrl);
        restDescriptor.getQueryParams().forEach((name, value) ->
                restBuilder.queryParam(name, jacksonUtils.writeValueAsText(value)));

        ResponseEntity<T> restRespone = restTemplate.exchange(restBuilder.buildAndExpand(restUriParams).encode().toUri(), restDescriptor.getHttpMethod(), restEntity, new ParameterizedTypeReference<T>() {
            @Override
            public Type getType() {
                return restDescriptor.getResultType();
            }
        });

        return restRespone.getBody();
    }

}
