package pl.fhframework.dp.commons.fh.print;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import pl.fhframework.dp.commons.utils.date.LocalDatesModule;
import pl.fhframework.core.logging.FhLogger;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.function.Supplier;

//import org.springframework.boot.web.client.RestTemplateBuilder;

@Configuration
public class PrintRestTemplateConfig {

    @Value("${print.rest.username:}")
    private String username;
    @Value("${print.rest.password:}")
    private String password;

    @Value("${rest.timeout:6000}")
    int timeout;

    @Value("${ssl.keystoreLocation:}")
    private String keystore;
    @Value("${ssl.keystorePassword:}")
    private String keystorePassword;


    public static RestTemplate restTemplate;

    @PostConstruct
    public void createRestTemplate() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        if(StringUtils.isEmpty(keystore)) {
            if (StringUtils.isEmpty(username)) {
                restTemplate = createSimpleRestTemplate();
            } else {
                restTemplate = createSimpleAuthenticationRestTemplate();
            }
        } else {
            restTemplate = createSslRestTemplate();
        }
    }

    private RestTemplate createSimpleAuthenticationRestTemplate() {
        return new RestTemplate(getAuthClientHttpRequestFactory());
    }

    private ClientHttpRequestFactory getAuthClientHttpRequestFactory() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials(username, password);
        provider.setCredentials(AuthScope.ANY, credentials);
        CloseableHttpClient client = HttpClientBuilder
                .create()
                .setDefaultCredentialsProvider(provider)
                .setDefaultRequestConfig(config)
                .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }

    private RestTemplate createSslRestTemplate() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        FhLogger.info("******** Keystore: {}", keystore);
        File keystoreFile = new File(keystore);
//        FhLogger.info("******** Truststore: {}", truststore);
//        File truststoreFile = new File(truststore);
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadKeyMaterial(keystoreFile, keystorePassword.toCharArray(), keystorePassword.toCharArray())
//                .loadTrustMaterial(truststoreFile, truststorePassword.toCharArray())
                .build();
        FhLogger.info("******** SSL Context protocol: {}", sslContext.getProtocol());
        HttpClient client = HttpClients.custom()
                .setSSLHostnameVerifier((hostname, session) -> {return true;})
                .setSSLContext(sslContext)
                .build();
        Supplier<ClientHttpRequestFactory> factory = () -> new HttpComponentsClientHttpRequestFactory(client);
        RestTemplateBuilder builder = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(10000L))
                .setReadTimeout(Duration.ofMillis(10000L))
                .requestFactory(factory);
        return builder.build();
    }


    /** Creates simple rest template */
    private RestTemplate createSimpleRestTemplate() {
        RestTemplate ret = new RestTemplate(getClientHttpRequestFactory());
        ret.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter());
        return ret;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        RequestConfig config = RequestConfig.custom()
            .setConnectTimeout((int)Duration.ofMinutes(1L).toMillis())
            .setConnectionRequestTimeout((int)Duration.ofMinutes(1L).toMillis())
            .setSocketTimeout((int)Duration.ofMinutes(15L).toMillis())
            .build();
        CloseableHttpClient client = HttpClientBuilder
            .create()
            .setDefaultRequestConfig(config)
            .build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }

    private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new LocalDatesModule());
        converter.setObjectMapper(mapper);
        return converter;
    }


}
