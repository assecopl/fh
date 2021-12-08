package pl.fhframework.dp.security.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import pl.fhframework.dp.security.repositories.RoleESRepository;

/**
 * @author Tomasz Kozlowski (created on 26.01.2021)
 */
@Configuration
@TestPropertySource("classpath:test.properties")
public class RoleTestConfig {

    @Bean
    public RoleService roleService(RoleESRepository roleESRepository) {
        return new RoleService(roleESRepository);
    }

}
