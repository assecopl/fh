package pl.fhframework.core.security.model;

import java.util.List;

public class NoneBusinessRole implements IBusinessRole {
    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String getRoleName() {
        return IBusinessRole.NONE;
    }

    @Override
    public void setRoleName(String roleName) {

    }

    @Override
    public String getDescription() {
        return null;
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
        return null;
    }

    @Override
    public List<IBusinessRole> getParentRoles() {
        return null;
    }
}
