package pl.fhframework.core.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configurer of a REST interceptor which attaches user session.
 */
@Configuration
public class RestUserSessionAttacherConfigurer implements WebMvcConfigurer {

    @Autowired
    private RestUserSessionAttacher attacherInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(attacherInterceptor);
    }
}
