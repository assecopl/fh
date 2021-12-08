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
import pl.fhframework.dp.transport.roles.RoleDto;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Tomasz Kozlowski (created on 26.01.2021)
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ElasticSearchConfig.class, RoleTestConfig.class})
@EnableConfigurationProperties
@TestPropertySource("classpath:test.properties")
@Slf4j
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    private static final String ROLE_NAME = "Role B";

    @Test
    public void fakeTest() {

    }

//    @Before
    //@Test
    public void saveRole() {
        RoleDto role = new RoleDto();
        role.setRoleName(ROLE_NAME);
        role.setDescription("Test role...");
        RoleDto savedRole = roleService.saveRole(role);

        assertNotNull(savedRole);
        log.info("Saved role: {}", savedRole.getId());
    }

    //@Test
    public void findAllRoles() {
        List<RoleDto> result = roleService.findAllRoles();

        assertFalse(result.isEmpty());
        log.info("Found roles: {}", result.size());
    }

    //@Test
    public void findRoleByName() {
        Optional<RoleDto> result = roleService.findRole(ROLE_NAME);

        assertTrue(result.isPresent());
        log.info("Found role: {}", result.get().getId());
    }

//    @After
    public void deleteRoles() {
        roleService.findAllRoles().forEach(role -> {
            roleService.deleteRole(role);
            log.info("Deleted role: {}", role.getId());
        });

        List<RoleDto> result = roleService.findAllRoles();
        assertTrue(result.isEmpty());
    }

    //@Test
    public void addDefaultRoles() {
        RoleDto role = new RoleDto();
        role.setRoleName("Administrator");
        role.setDescription("Role of system administrator");
        roleService.saveRole(role);
        log.info("Added default role: {}", role.getRoleName());
    }

    //@Test
    public void addSupportRoles() {
        RoleDto role = new RoleDto();
        role.setRoleName("Support");
        role.setDescription("Role of system support");
        roleService.saveRole(role);
        log.info("Added default role: {}", role.getRoleName());
    }

}
