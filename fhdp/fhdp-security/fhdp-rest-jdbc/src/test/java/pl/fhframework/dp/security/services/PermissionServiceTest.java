package pl.fhframework.dp.security.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pl.fhframework.dp.commons.camunda.ElasticSearchConfig;
import pl.fhframework.dp.transport.permissions.PermissionDto;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Tomasz Kozlowski (created on 27.01.2021)
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ElasticSearchConfig.class, PermissionTestConfig.class})
@EnableConfigurationProperties
@TestPropertySource("classpath:test.properties")
@Slf4j
public class PermissionServiceTest {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private static final String ROLE_NAME = "Role B";

    @Test
    public void fakeTest() {

    }

//    @Before
    //@Test
    public void savePermissions() {
        final String[] functions = {"test/function/view", "test/function/edit", "test/function/delete"};
        final String moduleUUID = "c8cd04f8-b6e3-48b3-90ac-b5a34332760c";
        for (String functionName : functions) {
            PermissionDto savedPermission = createPermission(ROLE_NAME, functionName, moduleUUID);

            assertNotNull(savedPermission);
            log.info("Saved permission: {}", savedPermission.getId());
        }
    }

    @SuppressWarnings("SameParameterValue")
    private PermissionDto createPermission(String roleName, String functionName, String moduleUUID) {
        PermissionDto permission = new PermissionDto();
        permission.setBusinessRoleName(roleName);
        permission.setFunctionName(functionName);
        permission.setModuleUUID(moduleUUID);
        permission.setDenial(false);
        permission.setCreatedBy("System");
        permission.setCreationDate(LocalDate.now());
        return permissionService.savePermission(permission);
    }

    //@Test
    public void findPermissionsForRole() {
        List<PermissionDto> result = permissionService.findPermissionsForRole(ROLE_NAME);

        assertEquals(3, result.size());
        log.info("Found permissions: {}", result.size());
    }

    //@Test
    public void findAllPermissions() {
        List<PermissionDto> result = permissionService.findAllPermissions();

        assertFalse(result.isEmpty());
        log.info("Found permissions: {}", result.size());
    }

    //@Test
    public void updatePermissionsForRole() {
        final String newRoleName = "NEW_ROLE";
        permissionService.updatePermissionsForRole(ROLE_NAME, newRoleName);
        List<PermissionDto> result = permissionService.findPermissionsForRole(ROLE_NAME);
        assertEquals(0, result.size());
        result = permissionService.findPermissionsForRole(newRoleName);
        assertEquals(3, result.size());
    }

//    @After
    public void deletePermissions() {
        permissionService.findAllPermissions().forEach(permission -> {
            permissionService.deletePermission(permission);
            log.info("Deleted permission: {}", permission.getId());
        });

        List<PermissionDto> result = permissionService.findPermissionsForRole(ROLE_NAME);
        assertTrue(result.isEmpty());
    }

    //@Test
    public void addDefaultPermissions() {
        if(elasticsearchOperations.indexOps(PermissionDto.class).exists()) {
            elasticsearchOperations.indexOps(PermissionDto.class).delete();
        }
        final String roleName = "Administrator";
        createPermission(roleName, "reportsCore", "557f04b2-0ccb-4800-89a1-6ef1bd531a6a");
        createPermission(roleName, "core", "557f04b2-0ccb-4800-89a1-6ef1bd531a6a");
        createPermission(roleName, "sessionNeverExpires", "557f04b2-0ccb-4800-89a1-6ef1bd531a6a");
        createPermission(roleName, "designer", "06e72cae-5d0c-45de-a4e1-24c2825515d9");
        createPermission(roleName, "fh/documentation", "0d4e165b-16e0-4da3-b6e3-7ef42987da1a");
        createPermission(roleName, "sam", "6a01406b-62a8-49fd-af55-a79df19c8950");
        log.info("Added default permissions for role {}", roleName);
    }

    //@Test
    public void addImdasAdmPermissions() {
        final String roleName = "Support";
        createPermission(roleName, "administration/permissions", "bed4b55f-a00c-4c28-814e-c07ecd0d76cf");
        log.info("Added default permissions for role {}", roleName);
    }

    //@Test
    public void addImdasViewerPermissions() {
        final String roleName = "Declaration viewer";
        createPermission(roleName, "declaration/view", "f823c43d-63f7-4d06-8188-c92c16da9d60");
        log.info("Added default permissions for role {}", roleName);
    }

}
