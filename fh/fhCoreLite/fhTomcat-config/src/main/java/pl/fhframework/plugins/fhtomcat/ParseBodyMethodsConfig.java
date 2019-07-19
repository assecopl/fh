package pl.fhframework.plugins.fhtomcat;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * Created by pawel.ruta on 2018-04-17.
 */
@Component
public class ParseBodyMethodsConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Override
    public void customize(ConfigurableWebServerFactory configurableEmbeddedServletContainer) {
        ((TomcatServletWebServerFactory)configurableEmbeddedServletContainer).addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                connector.setParseBodyMethods("POST,PUT");
            }
        });
    }
}
