package pl.fhframework.dp.commons.fh.adm.ui;

import lombok.RequiredArgsConstructor;
import pl.fhframework.dp.commons.fh.adm.i18n.AdmMessageHelper;
import pl.fhframework.dp.commons.fh.adm.security.FhAdmSystemFunction;
import pl.fhframework.dp.commons.fh.adm.service.AdmRoleManagementService;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.dp.transport.roles.RoleDto;
import pl.fhframework.SessionManager;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.security.provider.service.SecurityDataProvider;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.event.dto.NotificationEvent;

import java.time.LocalDate;
import java.util.*;

/**
 * @author Tomasz Kozlowski (created on 23.02.2021)
 */
@UseCase
@RequiredArgsConstructor
@SystemFunction(FhAdmSystemFunction.ADM_PERMISSIONS_VIEW)
public class PermissionsListUC extends FhdpBaseUC implements IInitialUseCase {

    private PermissionsListForm.Model model;

    private final SecurityDataProvider securityDataProvider; // for permissions management
    private final AdmRoleManagementService roleManagementService; // for permissions roles management (not roles from security data provider)
    private final AdmMessageHelper messageHelper;

    @Override
    public void start() {
        model = new PermissionsListForm.Model(securityDataProvider.getAllModules());
        refreshRoles();
        showForm(PermissionsListForm.class, model);
    }

    @Action
    public void roleSelected() {
        refreshPermissions();
    }

    private void refreshRoles() {
        List<RoleDto> roles = roleManagementService.findAllRoles();
        Collections.sort(roles);
        model.setRoles(roles);
    }

    private void refreshPermissions() {
        RoleDto selectedRole = model.getSelectedRole();
        if (selectedRole != null) {
            model.refreshPermissions(
                    securityDataProvider.findPermissionsForRole(convertToBusinessRole(selectedRole))
            );
        } else {
            model.refreshPermissions(Collections.emptyList());
        }
    }

    @Action
    public void close() {
        exit();
    }

    @Action
    @SystemFunction(FhAdmSystemFunction.ADM_PERMISSIONS_EDIT)
    public void addRole() {
        runSubUseCase(CreateRoleUC.class, IUseCaseSaveCancelCallback.getCallback(this::roleCreated));
    }

    public void roleCreated(RoleDto roleDto) {
        refreshRoles();
        model.setSelectedRole(roleDto);
        roleSelected();
        afterRolesChanged(AdmMessageHelper.ROLE_SAVED_MESSAGE);
    }

    @Action
    @SystemFunction(FhAdmSystemFunction.ADM_PERMISSIONS_EDIT)
    public void deleteRole(RoleDto role) {
        securityDataProvider.findPermissionsForRole(convertToBusinessRole(role)) // needed to clear permission cache
                .forEach(securityDataProvider::deletePermission);
        roleManagementService.deleteRole(role);
        afterRolesChanged(AdmMessageHelper.ROLE_DELETED_MESSAGE);
    }

    @Action
    @SystemFunction(FhAdmSystemFunction.ADM_PERMISSIONS_EDIT)
    public void addPermission() {
        runSubUseCase(FunctionsSelectUC.class, IUseCaseSaveCancelCallback.getCallback(this::createPermissions));
    }

    public void createPermissions(Set<AuthorizationManager.Function> functions) {
        Collection<IPermission> selectedRolePermissions = model.getPermissionsCollection();
        List<IPermission> newPermissions = new ArrayList<>();
        boolean exists;
        for (AuthorizationManager.Function function : functions) {
            exists = selectedRolePermissions.stream()
                        .anyMatch(permission -> this.isPermissionMatch(permission, function));

            if (!exists) {
                newPermissions.add(
                    createPermission(model.getSelectedRole(), function.getName(), function.getModuleUUID(), function.isDenial())
                );
            }
        }

        if (!newPermissions.isEmpty()) {
            securityDataProvider.savePermissions(newPermissions);
            afterPermissionsChanged(AdmMessageHelper.PERMISSION_SAVED_MESSAGE);
        }
    }

    private boolean isPermissionMatch(IPermission permission, AuthorizationManager.Function function) {
        return StringUtils.equals(function.getName(), permission.getFunctionName())
                && StringUtils.equals(function.getModuleUUID(), permission.getModuleUUID())
                && Objects.equals(function.isDenial(), permission.getDenial());
    }

    private IPermission createPermission(RoleDto roleDto, String functionName, String moduleUUID, boolean denial) {
        IPermission permission = securityDataProvider.createSimplePermissionInstance(roleDto.getRoleName(), functionName, moduleUUID);
        permission.setCreationDate(LocalDate.now());
        permission.setCreatedBy(SessionManager.getUserLogin());
        permission.setDenial(denial);
        return permission;
    }

    @Action
    @SystemFunction(FhAdmSystemFunction.ADM_PERMISSIONS_EDIT)
    public void deletePermission(IPermission permission) {
        securityDataProvider.deletePermission(permission);
        afterPermissionsChanged(AdmMessageHelper.PERMISSION_DELETED_MESSAGE);
    }

    private void afterRolesChanged(String messageKey) {
        refreshRoles();
        if (!StringUtils.isNullOrEmpty(messageKey)) {
            messageHelper.notifyMessage(NotificationEvent.Level.SUCCESS, messageKey);
        }
    }

    private void afterPermissionsChanged(String messageKey) {
        securityDataProvider.invalidatePermissionCacheForRole(convertToBusinessRole(model.getSelectedRole()));
        refreshPermissions();
        if (!StringUtils.isNullOrEmpty(messageKey)) {
            messageHelper.notifyMessage(NotificationEvent.Level.SUCCESS, messageKey);
        }
    }

    private IBusinessRole convertToBusinessRole(RoleDto roleDto) {
        return securityDataProvider.createSimpleBusinessRoleInstance(roleDto.getRoleName());
    }

}
