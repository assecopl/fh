package pl.fhframework.plugins.fhtomcat;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Configures additional SSL server port for client SSL certificate authentication.
 */
@Component
public class CertAuthServerConnectorConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    private static final Logger LOGGER = Logger.getLogger(CertAuthServerConnectorConfig.class.getName());

    @Value("${fhframework.certAuth.server.port:-1}")
    private int port;

    @Value("${fhframework.certAuth.server.keystore:}")
    private String keystore;

    @Value("${fhframework.certAuth.server.keystorePass:}")
    private String keystorePass;

    @Value("${fhframework.certAuth.server.keystoreType:}")
    private String keystoreType;

    @Value("${fhframework.certAuth.server.keystoreAlias:}")
    private String keystoreAlias;

    @Value("${fhframework.certAuth.server.truststore:}")
    private String truststore;

    @Value("${fhframework.certAuth.server.truststorePass:}")
    private String truststorePass;

    @Value("${fhframework.certAuth.server.truststoreType:}")
    private String truststoreType;

    @Override
    public void customize(ConfigurableWebServerFactory genericContainerFactory) {
        if (port != -1 || !keystore.isEmpty() || !keystorePass.isEmpty() || !keystoreType.isEmpty() || !keystoreAlias.isEmpty()
                || !truststore.isEmpty() || !truststorePass.isEmpty() || !truststoreType.isEmpty()) {

            if (port == -1 || keystore.isEmpty() || keystorePass.isEmpty()) {
                LOGGER.severe("At least fhframework.certAuth.server.port, .keystore and .keystorePass properties must be set");
                return;
            }

            if (!(genericContainerFactory instanceof TomcatServletWebServerFactory)) {
                LOGGER.severe("Container factory is not a " + TomcatServletWebServerFactory.class.getSimpleName()
                        + ", got " + genericContainerFactory.getClass().getName());
                return;
            }

            TomcatServletWebServerFactory containerFactory = (TomcatServletWebServerFactory) genericContainerFactory;

            LOGGER.info("Adding client SSL certificate authentication connector at port " + port);

            Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(port);
            protocol.setSSLEnabled(true);
            protocol.setClientAuth(SSLHostConfig.CertificateVerification.REQUIRED.name());
            protocol.setKeystoreFile(keystore);
            protocol.setKeystorePass(keystorePass);
            if (!keystoreType.isEmpty()) {
                protocol.setKeystoreType(keystoreType);
            }
            String finalTruststore;
            String finalTruststorePass;
            String finalTruststoreType;
            if (!truststore.isEmpty()) {
                finalTruststore = truststore;
                finalTruststorePass = truststorePass;
                finalTruststoreType = truststoreType;
            } else {
                finalTruststore = keystore;
                finalTruststorePass = keystorePass;
                finalTruststoreType = keystoreType;
                LOGGER.info("Property fhframework.certAuth.server.truststore not set, using keystore as truststore");
            }
            protocol.setTruststoreFile(finalTruststore);
            protocol.setTruststorePass(finalTruststorePass);
            if (!finalTruststoreType.isEmpty()) {
                protocol.setTruststoreType(finalTruststoreType);
            }
            containerFactory.addAdditionalTomcatConnectors(connector);
        }
    }
}