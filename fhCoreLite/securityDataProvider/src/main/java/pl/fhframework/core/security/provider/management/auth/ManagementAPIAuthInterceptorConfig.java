package pl.fhframework.core.security.provider.management.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.fhframework.core.security.provider.management.api.ManagementAPIController;
import pl.fhframework.core.security.provider.management.api.SecurityManagementAPIController;

/**
 * Created by Piotr on 2018-05-16.
 */
@Configuration
@Order(1000)
@ConditionalOnProperty(name = "fhframework.managementApi.enabled", havingValue = "true")
public class ManagementAPIAuthInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private ManagementAPIAuthInterceptor managementAPIAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(managementAPIAuthInterceptor).addPathPatterns(
                ManagementAPIController.MANAGEMENT_API_URI + "/**",
                SecurityManagementAPIController.SECURITY_MANAGEMENT_API_URI + "/**"
        );
    }
}
