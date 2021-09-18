package pl.fhframework.core.security.provider.remote.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author Tomasz Kozlowski (created on 20.05.2019)
 */
@Getter
@Setter
public class RoleInstance implements IRoleInstance {

    private Long id;
    private LocalDate assignmentTime;
    private LocalDate validFrom;
    private LocalDate validTo;
    private IBusinessRole businessRole;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleInstance)) return false;
        RoleInstance that = (RoleInstance) o;
        if (getId() == null || that.getId() == null) {
            return Objects.equals(assignmentTime, that.assignmentTime) &&
                    Objects.equals(validFrom, that.validFrom) &&
                    Objects.equals(validTo, that.validTo) &&
                    Objects.equals(businessRole, that.businessRole);
        }
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : Objects.hash(assignmentTime, validFrom, validTo, businessRole);
    }

}
