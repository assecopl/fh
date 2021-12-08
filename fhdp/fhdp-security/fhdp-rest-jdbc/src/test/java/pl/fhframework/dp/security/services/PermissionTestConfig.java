package pl.fhframework.dp.security.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import pl.fhframework.dp.security.repositories.PermissionESRepository;

/**
 * @author Tomasz Kozlowski (created on 26.01.2021)
 */
@Configuration
@TestPropertySource("classpath:test.properties")
public class PermissionTestConfig {

    @Bean
    public PermissionService permissionService(PermissionESRepository permissionESRepository) {
        return new PermissionService(permissionESRepository);
    }

}
