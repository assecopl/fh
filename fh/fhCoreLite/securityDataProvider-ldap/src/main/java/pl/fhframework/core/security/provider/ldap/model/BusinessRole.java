package pl.fhframework.core.security.provider.ldap.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author tomasz.kozlowski (created on 2018-06-13)
 */
@Setter
@Getter
public class BusinessRole implements IBusinessRole {

    private Long id;
    private String roleName;
    private String description;
    private boolean rootRole = true;

    @Override
    public void addSubRole(IBusinessRole subRole) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSubRole(IBusinessRole subRole) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<IBusinessRole> getSubRoles() {
        return Collections.emptyList();
    }

    @Override
    public List<IBusinessRole> getParentRoles() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessRole that = (BusinessRole) o;
        return StringUtils.equalsIgnoreCase(roleName, that.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName);
    }

}
