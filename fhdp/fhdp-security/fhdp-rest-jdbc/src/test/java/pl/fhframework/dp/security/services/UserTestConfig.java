package pl.fhframework.dp.security.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import pl.fhframework.dp.security.repositories.UserESRepository;

/**
 * @author Tomasz Kozlowski (created on 28.01.2021)
 */
@Configuration
@TestPropertySource("classpath:test.properties")
public class UserTestConfig {

    @Bean
    public UserService userService(UserESRepository userESRepository) {
        return new UserService(userESRepository);
    }

}
