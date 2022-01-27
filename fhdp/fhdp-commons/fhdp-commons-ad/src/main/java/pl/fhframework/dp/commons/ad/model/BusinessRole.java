package pl.fhframework.dp.commons.ad.model;

import pl.fhframework.core.security.model.IBusinessRole;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 08.01.2020
 */
public class BusinessRole implements IBusinessRole {
    private Long id;
    private String roleName;
    private String description;

    public BusinessRole(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getRoleName() {
        return roleName;
    }

    @Override
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public boolean isRootRole() {
        return true;
    }

    @Override
    public void setRootRole(boolean rootRole) {

    }

    @Override
    public void addSubRole(IBusinessRole subRole) {

    }

    @Override
    public void removeSubRole(IBusinessRole subRole) {

    }

    @Override
    public List<IBusinessRole> getSubRoles() {
        return new ArrayList<>();
    }

    @Override
    public List<IBusinessRole> getParentRoles() {
        return new ArrayList<>();
    }
}
