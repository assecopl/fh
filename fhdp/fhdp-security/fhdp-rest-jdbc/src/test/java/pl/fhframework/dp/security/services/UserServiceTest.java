package pl.fhframework.dp.security.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pl.fhframework.dp.commons.camunda.ElasticSearchConfig;
import pl.fhframework.dp.transport.login.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Tomasz Kozlowski (created on 28.01.2021)
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ElasticSearchConfig.class, UserTestConfig.class})
@EnableConfigurationProperties
@TestPropertySource("classpath:test.properties")
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    private static final String USERNAME = "john.doe";

    @Test
    public void fakeTest() {

    }

//    @Before
    public void saveUser() {
        UserDto user = new UserDto();
        user.setUsername(USERNAME);
        user.setPassword("qwe321");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.getRoles().add("Administrator");
        user.getAttributes().put("locale", "en_US");
        UserDto savedUser = userService.saveUser(user);

        assertNotNull(savedUser);
        log.info("Saved user: [{}] {}", savedUser.getUsername(), savedUser.getId());
    }

    //@Test
    public void findAllUsers() {
        List<UserDto> result = userService.findAllUsers();

        assertFalse(result.isEmpty());
        log.info("Found users: {}", result.size());
    }

    //@Test
    public void findUser() {
        Optional<UserDto> result = userService.findUser(USERNAME);

        assertTrue(result.isPresent());
        log.info("Found user: [{}] {}", result.get().getUsername(), result.get().getId());
    }

//    @After
    public void deleteUser() {
        userService.findAllUsers().forEach(user -> {
            userService.deleteUser(user);
            log.info("Deleted user: [{}] {}", user.getUsername(), user.getId());
        });

        List<UserDto> result = userService.findAllUsers();
        assertTrue(result.isEmpty());
    }

    //@Test
    public void addDefaultUser() {
        UserDto user = new UserDto();
        user.setUsername("admin");
        user.setPassword("qwe321");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.getRoles().add("Administrator");
        user.getAttributes().put("locale", "en_US");
        UserDto savedUser = userService.saveUser(user);
        log.info("Added default user: {}", savedUser.getUsername());
    }

}
