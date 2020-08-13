package pl.fhframework.core.security.certauth;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.fhframework.core.logging.FhLogger;

/**
 * Configures client side system properties for client SSL certificate authentication.
 */
@Component
public class CertAuthClientConfigurer implements InitializingBean {

    @Value("${fhframework.certAuth.client.keystore:}")
    private String keystore;

    @Value("${fhframework.certAuth.client.keystorePass:}")
    private String keystorePass;

    @Value("${fhframework.certAuth.client.keystoreType:}")
    private String keystoreType;

    @Value("${fhframework.certAuth.client.truststore:}")
    private String truststore;

    @Value("${fhframework.certAuth.client.truststorePass:}")
    private String truststorePass;

    @Value("${fhframework.certAuth.client.truststoreType:}")
    private String truststoreType;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!keystore.isEmpty() || !keystorePass.isEmpty() || !keystoreType.isEmpty()
                || !truststore.isEmpty() || !truststorePass.isEmpty() || !truststoreType.isEmpty()) {

            if (keystore.isEmpty() || keystorePass.isEmpty()) {
                FhLogger.error("At least fhframework.certAuth.client.keystore and .keystorePass properties must be set");
                return;
            }

            if (System.getProperty("javax.net.ssl.keyStore") != null) {
                FhLogger.warn("fhframework.certAuth.client.* will overrride values of javax.net.ssl.keyStore and javax.net.ssl.trustStore JVM system properties");
            }
            FhLogger.info(this.getClass(), "Setting standard JVM system properties for client SSL certificate authentication");

            System.setProperty("javax.net.ssl.keyStore", keystore);
            System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
            if (!keystoreType.isEmpty()) {
                System.setProperty("javax.net.ssl.keyStoreType", keystoreType);
            }
            String finalTruststore;
            String finalTruststorePass;
            String finalTruststoreType;
            if (!truststore.isEmpty()) {
                finalTruststore = truststore;
                finalTruststorePass = truststorePass;
                finalTruststoreType = truststoreType;
            } else {
                FhLogger.info(this.getClass(), "Property fhframework.certAuth.server.truststore not set, using keystore as truststore");

                finalTruststore = keystore;
                finalTruststorePass = keystorePass;
                finalTruststoreType = keystoreType;
            }
            System.setProperty("javax.net.ssl.trustStore", finalTruststore);
            System.setProperty("javax.net.ssl.trustStorePassword", finalTruststorePass);
            if (!finalTruststoreType.isEmpty()) {
                System.setProperty("javax.net.ssl.trustStoreType", finalTruststoreType);
            }
        }
    }
}
