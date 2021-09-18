package pl.fhframework.core.security.provider.jdbc.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IBusinessRole;
import pl.fhframework.core.security.model.IRoleInstance;
import pl.fhframework.jpa.converter.LocalDateAttributeConverter;
import pl.fhframework.fhPersistence.core.BasePersistentObject;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author tomasz.kozlowski (created on 2017-11-22)
 */
@Getter
@Setter
@Entity
@Table(name = "SEC_ROLE_INSTANCES")
@SequenceGenerator(name = "SEQUENCE_GENERATOR", sequenceName = "SEC_ROLE_INSTANCES_ID_SEQ")
public class RoleInstance extends BasePersistentObject implements IRoleInstance {

    @Column(name = "ASSIGNMENT_TIME", nullable = false)
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate assignmentTime;

    @Column(name = "VALID_FROM")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate validFrom;

    @Column(name = "VALID_TO")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate validTo;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "BUSINESS_ROLE_ID", nullable = false)
    private BusinessRole businessRole;

    @Override
    public void setBusinessRole(IBusinessRole businessRole) {
        if (businessRole instanceof BusinessRole) {
            this.businessRole = ((BusinessRole) businessRole);
        } else {
            throw new IllegalArgumentException(
                    String.format("businessRole parameter is not an instance of %s ! [businessRole type: %s]",
                            BusinessRole.class.getName(), businessRole.getClass().getName())
            );
        }
    }

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
