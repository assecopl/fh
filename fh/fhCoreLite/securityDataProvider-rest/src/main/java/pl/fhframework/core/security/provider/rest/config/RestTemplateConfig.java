package pl.fhframework.core.security.provider.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.web.client.RestTemplate;
import pl.fhframework.core.security.provider.enums.RestTemplateType;
import pl.fhframework.core.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Arrays;

@Configuration
public class RestTemplateConfig {

    @Value("${fhframework.security.provider.rest.templateType:Simple}")
    private RestTemplateType templateType;
    @Value("${fhframework.security.provider.rest.username:}")
    private String username;
    @Value("${fhframework.security.provider.rest.password:}")
    private String password;
    @Value("${fhframework.security.provider.rest.tokenUri:}")
    private String tokenUri;
    @Value("${fhframework.security.provider.rest.clientId:}")
    private String clientId;
    @Value("${fhframework.security.provider.rest.clientSecret:}")
    private String clientSecret;
    @Value("${fhframework.security.provider.rest.grantType:client_credentials}")
    private String grantType;
    @Value("${fhframework.security.provider.rest.scope:}")
    private String scope;

    public static RestTemplate restTemplate;

    @PostConstruct
    public void createRestTemplate() {
        switch (templateType) {
            case Simple: restTemplate = createSimpleRestTemplate();
                break;
            case OAuth2: restTemplate = createOAuth2RestTemplate();
                break;
        }
    }

    /** Creates simple rest template */
    private RestTemplate createSimpleRestTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(10000L))
                .setReadTimeout(Duration.ofMillis(10000L))
                .build();
    }

    /** Creates OAuth2 rest template */
    private RestTemplate createOAuth2RestTemplate() {
        BaseOAuth2ProtectedResourceDetails resourceDetails;
        if (StringUtils.isNullOrEmpty(username)) {
            resourceDetails = new ClientCredentialsResourceDetails();
        } else {
            resourceDetails = new ResourceOwnerPasswordResourceDetails();
            ((ResourceOwnerPasswordResourceDetails) resourceDetails).setUsername(StringUtils.emptyToNull(username));
            ((ResourceOwnerPasswordResourceDetails) resourceDetails).setPassword(StringUtils.emptyToNull(password));
        }
        resourceDetails.setAccessTokenUri(StringUtils.emptyToNull(tokenUri));
        resourceDetails.setClientId(StringUtils.emptyToNull(clientId));
        resourceDetails.setClientSecret(StringUtils.emptyToNull(clientSecret));
        resourceDetails.setGrantType(StringUtils.emptyToNull(grantType));
        resourceDetails.setScope(Arrays.asList(scope.split(",")));
        DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext();
        return new OAuth2RestTemplate(resourceDetails, clientContext);
    }

}
