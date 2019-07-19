package pl.fhframework.integration.core.client;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import org.aspectj.ajdt.internal.core.builder.AjState;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by pawel.ruta on 2018-10-17.
 */
@Data
@Builder
public class RestDescriptor {
    private String endpointOrUrl;

    private String uri;

    private String basicAuthentication;

    private Object body;

    private Type resultType;

    @Singular
    private Map<String, Object> headerParams;

    @Singular
    private Map<String, Object> uriParams;

    @Singular
    private Map<String, Object> queryParams;

    private HttpMethod httpMethod;
}
