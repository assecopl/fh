package pl.fhframework.core.security.provider.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import pl.fhframework.core.util.StringUtils;

/**
 * Created by pawel.ruta on 2018-04-17.
 */
@Component
public class SessionIdConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Value("${luna.security.provider.rest.jSessionIdName:}")
    private String jSessionIdName;

    @Override
    public void customize(ConfigurableWebServerFactory configurableWebServerFactory) {
        ((TomcatServletWebServerFactory)configurableWebServerFactory).addContextCustomizers((TomcatContextCustomizer) context -> {
            if (!StringUtils.isNullOrEmpty(jSessionIdName)) {
                context.setSessionCookieName(jSessionIdName);
            }
        });
    }

}
