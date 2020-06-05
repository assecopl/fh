package pl.fhframework.core.security.provider.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.IFunction;
import pl.fhframework.core.security.ISecurityDataProvider;
import pl.fhframework.core.security.SecurityProviderInitializer;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tomasz.Kozlowski (created on 29.08.2018)
 */
public abstract class AbstractSecurityProviderInitializer implements SecurityProviderInitializer {

    @Value("${fhframework.security.provider.default-admin-login:admin}")
    protected String defaultAdminLogin;
    @Value("${fhframework.security.provider.default-admin-pass:admin}")
    protected String defaultAdminPass;
    @Value("${fhframework.security.provider.default-admin-role:#{null}}")
    protected String defaultAdminRole;
    @Value("${fhframework.security.provider.default-admin-all-permissions:false}")
    protected boolean defaultAdminAllPermissions;
    @Value("${fhframework.security.provider.generate-default-data:false}")
    protected boolean generateDefaultData;
    @Value("${fh.web.guests.allowed:false}")
    protected boolean guestsAllowed;

    protected final ISecurityDataProvider securityDataProvider;

    protected AbstractSecurityProviderInitializer(ISecurityDataProvider securityDataProvider) {
        this.securityDataProvider = securityDataProvider;
    }

    @Override
    public void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        configureAuthentication(auth, Collections.emptyList());
    }

    /**
     * Creates default permissions for administrator role. If default administrator role
     * has any permissions then method does not do anything.
     */
    protected void createDefaultAdminPermissions() {
        createDefaultAdminPermissions(false);
    }

    /**
     * Creates default permissions for administrator role. If default administrator role
     * has any permissions then method does not do anything.
     *
     * @param createRole if <code>true</code> then creates new default administrator role if does not exists.
     */
    protected void createDefaultAdminPermissions(boolean createRole) {
        if (!StringUtils.isNullOrEmpty(defaultAdminRole)) {
            IBusinessRole adminRole = securityDataProvider.findBusinessRoleByName(defaultAdminRole);
            if (adminRole == null) {
                if (createRole) {
                    adminRole = securityDataProvider.createSimpleBusinessRoleInstance(defaultAdminRole);
                    securityDataProvider.saveBusinessRole(adminRole);
                } else {
                    FhLogger.info(this.getClass(), "Cannot find business role by name {}", defaultAdminRole);
                    return;
                }
            }
            List<IPermission> permissions = securityDataProvider.findPermissionsForRole(adminRole);
            if (permissions.isEmpty()) {
                if (defaultAdminAllPermissions) { // create permissions to all system functions
                    for (IFunction function : securityDataProvider.getAllSystemFunctions()) {
                        permissions.add(securityDataProvider.createSimplePermissionInstance(
                                adminRole.getRoleName(), function.getName(), function.getModuleUUID()));
                    }
                } else { // create permissions only for administration system functions
                    permissions = getDefaultAdminPermissions(adminRole.getRoleName());
                }
                permissions.forEach(permission -> {
                    permission.setCreationDate(LocalDate.now());
                    permission.setCreatedBy("system");
                });
                securityDataProvider.savePermissions(permissions);
            }
        }
    }

    /**
     * Return default FH application permissions for administrator role.
     *
     * @param adminRoleName administrator business role name.
     * @return default FH application permissions for administrator role.
     */
    protected List<IPermission> getDefaultAdminPermissions(String adminRoleName) {
        List<IPermission> permissions = new ArrayList<>();
        permissions.add(securityDataProvider.createSimplePermissionInstance(
                adminRoleName, "sam/security", "6a01406b-62a8-49fd-af55-a79df19c8950"));
        permissions.add(securityDataProvider.createSimplePermissionInstance(
                adminRoleName, "designer", "06e72cae-5d0c-45de-a4e1-24c2825515d9"));
        permissions.add(securityDataProvider.createSimplePermissionInstance(
                adminRoleName, "fh/documentation", "0d4e165b-16e0-4da3-b6e3-7ef42987da1a"));
        return permissions;
    }

    /** Creates Guest role is guests are allowed */
    protected void createGuestRole() {
        if (guestsAllowed && securityDataProvider.findBusinessRoleByName(IBusinessRole.GUEST) == null) {
            securityDataProvider.saveBusinessRole(
                    securityDataProvider.createSimpleBusinessRoleInstance(IBusinessRole.GUEST)
            );
        }
    }

}
