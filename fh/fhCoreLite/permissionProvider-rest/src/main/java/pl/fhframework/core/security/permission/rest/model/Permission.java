package pl.fhframework.core.security.permission.rest.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IPermission;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Tomasz Kozlowski (created on 17.10.2019)
 */
@Getter
@Setter
public class Permission implements IPermission {

    private Long id;
    private String businessRoleName;
    private String functionName;
    private String moduleUUID;
    private LocalDate creationDate;
    private String createdBy;
    private Boolean denial;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        if (id == null || that.id == null) {
            return Objects.equals(businessRoleName, that.businessRoleName) &&
                    Objects.equals(functionName, that.functionName) &&
                    Objects.equals(moduleUUID, that.moduleUUID) &&
                    Objects.equals(denial, that.denial);
        }
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : Objects.hash(businessRoleName, functionName, moduleUUID, denial);
    }

}
