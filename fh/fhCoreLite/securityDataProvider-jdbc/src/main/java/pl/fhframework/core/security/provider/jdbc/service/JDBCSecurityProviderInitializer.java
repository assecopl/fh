package pl.fhframework.core.security.provider.jdbc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.fhframework.core.security.*;
import pl.fhframework.core.security.model.*;
import pl.fhframework.core.security.provider.service.AbstractSecurityProviderInitializer;
import pl.fhframework.core.util.StringUtils;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author tomasz.kozlowski (created on 2017-12-05)
 */
@Service
@PropertySource("classpath:security-provider.properties")
public class JDBCSecurityProviderInitializer extends AbstractSecurityProviderInitializer {

    @Value("${fhframework.security.provider.jdbc.query.users}")
    private String usernameQuery;
    @Value("${fhframework.security.provider.jdbc.query.user-roles}")
    private String userRolesQuery;

    private final DataSource dataSource;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public JDBCSecurityProviderInitializer(ISecurityDataProvider securityDataProvider, DataSource dataSource) {
        super(securityDataProvider);
        this.dataSource = dataSource;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void configureAuthentication(AuthenticationManagerBuilder auth, List<IDefaultUser> defaultUsers) throws Exception {
        auth
            .jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery(usernameQuery)
            .authoritiesByUsernameQuery(userRolesQuery)
            .passwordEncoder(passwordEncoder);

        if (generateDefaultData) {
            if (CollectionUtils.isEmpty(defaultUsers)) {
                defaultUsers = generateDefaultUsers();
            }
            if (!CollectionUtils.isEmpty(defaultUsers)) {
                generateDefaultRoles(defaultUsers);
                generateDefaultUsers(defaultUsers);
            }
        }

        createGuestRole();
    }

    private List<IDefaultUser> generateDefaultUsers() {
        if (!StringUtils.isNullOrEmpty(defaultAdminRole)) {
            DefaultRole adminRole = new DefaultRole(defaultAdminRole, getDefaultAdminFunctions(defaultAdminRole));
            return Collections.singletonList(
                    new DefaultUser(defaultAdminLogin, defaultAdminPass, adminRole)
            );
        } else {
            return Collections.emptyList();
        }
    }

    private IFunction[] getDefaultAdminFunctions(String adminRoleName) {
        return getDefaultAdminPermissions(adminRoleName).stream()
                .map(permission -> AuthorizationManager.Function.of(permission.getFunctionName(), permission.getModuleUUID()))
                .collect(Collectors.toList()).toArray(new IFunction[]{});
    }

    private void generateDefaultRoles(List<IDefaultUser> defaultUsers) {
        List<IBusinessRole> roles = securityDataProvider.findAllBusinessRoles();
        if (roles.isEmpty()) {
            // create roles
            Set<IDefaultRole> defaultRoles = new HashSet<>();
            defaultUsers.forEach(
                    user -> defaultRoles.addAll(user.getRoles())
            );
            defaultRoles.forEach(
                    role -> roles.add(securityDataProvider.createSimpleBusinessRoleInstance(role.getName()))
            );
            securityDataProvider.saveBusinessRoles(roles);

            // creates permissions for all defined roles
            List<IPermission> permissions;
            for (IDefaultRole defaultRole : defaultRoles) {
                permissions = new ArrayList<>();
                IBusinessRole businessRole = securityDataProvider.findBusinessRoleByName(defaultRole.getName());

                Set<IFunction> functions;
                if (defaultRole.isAllFunctions()) {
                    functions = new HashSet<>(securityDataProvider.getAllSystemFunctions());
                } else {
                    functions = defaultRole.getFunctions();
                }

                for (IFunction function : functions) {
                    permissions.add(
                            createPermission(businessRole, function.getName(), function.getModuleUUID())
                    );
                }
                securityDataProvider.savePermissions(permissions);
            }
        }
    }

    private void generateDefaultUsers(List<IDefaultUser> defaultUsers) {
        if (securityDataProvider.getUserAccountsCount() == 0) {
            List<IUserAccount> userAccounts = new ArrayList<>();
            for (IDefaultUser defaultUser : defaultUsers) {
                IUserAccount user =
                        createUser(
                            defaultUser.getLogin(),
                            defaultUser.getFirstName(),
                            defaultUser.getLastName(),
                            passwordEncoder.encode(defaultUser.getPassword()),
                            createRoleInstances(defaultUser.getRoles())
                        );
                userAccounts.add(user);
            }
            securityDataProvider.saveUserAccounts(userAccounts);
        }
    }

    private IUserAccount createUser(String login, String firstName, String lastName, String password, List<IRoleInstance> roles) {
        IUserAccount userAccount = securityDataProvider.createSimpleUserAccountInstance();
        userAccount.setLogin(login);
        userAccount.setFirstName(firstName);
        userAccount.setLastName(lastName);
        userAccount.setPassword(password);
        userAccount.setEmail(login.toLowerCase() + "@mail.com");
        userAccount.setBlocked(false);
        userAccount.setDeleted(false);
        roles.forEach(userAccount::addRole);
        return userAccount;
    }

    private List<IRoleInstance> createRoleInstances(Set<IDefaultRole> defaultRoles) {
        List<IRoleInstance> result = new ArrayList<>();
        IBusinessRole businessRole;
        for (IDefaultRole defaultRole : defaultRoles) {
            businessRole = securityDataProvider.findBusinessRoleByName(defaultRole.getName());
            IRoleInstance roleInstance = securityDataProvider.createSimpleRoleInstance();
            roleInstance.setAssignmentTime(LocalDate.now());
            roleInstance.setValidFrom(LocalDate.now());
            roleInstance.setBusinessRole(businessRole);
            result.add(roleInstance);
        }
        return result;
    }

    private IPermission createPermission(IBusinessRole businessRole, String functionName, String moduleUUID) {
        IPermission permission = securityDataProvider.createSimplePermissionInstance(businessRole.getRoleName(), functionName, moduleUUID);
        permission.setCreationDate(LocalDate.now());
        permission.setCreatedBy("system");
        return permission;
    }

}
