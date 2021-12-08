package pl.fhframework.dp.commons.fh.adm.ui;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.roles.RoleDto;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.AccessibilityEnum;
import pl.fhframework.model.forms.CollectionPageableModel;
import pl.fhframework.model.forms.Form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tomasz Kozlowski (created on 23.02.2021)
 */
public class PermissionsListForm extends Form<PermissionsListForm.Model> {

    @Getter
    @Setter
    public static class Model {
        private List<RoleDto> roles;
        private RoleDto selectedRole;
        private CollectionPageableModel<IPermission> permissions = new CollectionPageableModel<>(new ArrayList<>());
        private List<IPermission> selectedPermissions = new ArrayList<>();
        private List<AuthorizationManager.Module> allModules;

        public Model(List<AuthorizationManager.Module> allModules) {
            this.allModules = allModules;
        }

        public void refreshPermissions(List<IPermission> rolePermissions) {
            permissions.setCollection(rolePermissions);
            selectedPermissions.clear();
        }

        public Collection<IPermission> getPermissionsCollection() {
            return permissions.getCollection();
        }

        public String getModuleName(String moduleUUID) {
            return allModules.stream()
                    .filter(module -> StringUtils.equals(module.getUuid(), moduleUUID))
                    .map(AuthorizationManager.Module::getName)
                    .findAny().orElse(moduleUUID);
        }

    }

    public AccessibilityEnum isAdminRole(RoleDto role) {
        if(role.getRoleName().toLowerCase().contains("admin")) return AccessibilityEnum.VIEW;
        else return AccessibilityEnum.EDIT;
    }

}
