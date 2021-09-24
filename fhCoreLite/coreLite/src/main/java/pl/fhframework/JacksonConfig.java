package pl.fhframework;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import pl.fhframework.core.FhException;
import pl.fhframework.core.maps.features.json.GeoJsonModule;
import pl.fhframework.core.util.JacksonUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Created by pawel.ruta on 2018-04-18.
 */
@Configuration
public class JacksonConfig {
    @Value("${fhframework.managementApi.certAuth.enabled:false}")
    private boolean authorizationEnabled;

    @Value("${fhframework.ssl.ignore.rest:false}")
    private boolean sslIgnoreRest;

    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter(ObjectMapper objectMapper) {
        return new FhJacksonMapping(objectMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.serializationInclusion(JsonInclude.Include.NON_EMPTY);
        builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
        //builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        builder.dateFormat(new ISO8601DateFormat());

        builder.modulesToInstall(new GeoJsonModule());

        return builder.build();
    }

    @Bean
    public RestTemplate restTemplate(MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter) {
        RestTemplate restTemplate = new RestTemplate(Arrays.asList(mappingJacksonHttpMessageConverter, new FormHttpMessageConverter()));

        SSLConnectionSocketFactory csf;

        if (authorizationEnabled && !sslIgnoreRest) {
            try {
                csf = new SSLConnectionSocketFactory(SSLContext.getDefault());
            } catch (Exception e) {
                throw new FhException(e);
            }
        }
        else {
            // todo: temporary disable ssl check (otherwise use keystore)
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

            SSLContext sslContext;
            try {
                sslContext = org.apache.http.ssl.SSLContexts.custom()
                        .loadTrustMaterial(null, acceptingTrustStrategy)
                        .build();
            } catch (Exception e) {
                throw new FhException(e);
            }

            csf = new SSLConnectionSocketFactory(sslContext);
        }

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    private static class FhJacksonMapping extends MappingJackson2HttpMessageConverter {
        public FhJacksonMapping(ObjectMapper objectMapper) {
            super(objectMapper);
        }

        @Override
        protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            if (JacksonUtils.isHttpRequest()) {
                Object jsonValue = RequestContextHolder.getRequestAttributes().getAttribute(JacksonUtils.ATTR_NAME, RequestAttributes.SCOPE_REQUEST);
                if (jsonValue != null) {
                    object = jsonValue;
                    RequestContextHolder.getRequestAttributes().removeAttribute(JacksonUtils.ATTR_NAME, RequestAttributes.SCOPE_REQUEST);
                }
            }
            super.writeInternal(object, type, outputMessage);
        }
    }
}
