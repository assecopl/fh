package pl.fhframework.compiler.cache;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by pawel.ruta on 2018-02-15.
 */
@Component
@Getter
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode= ScopedProxyMode.TARGET_CLASS)
public class UserSessionCache {
    @Autowired
    private RulesListCache rulesListCache;

    @Autowired
    private ServicesListCache servicesListCache;
}
