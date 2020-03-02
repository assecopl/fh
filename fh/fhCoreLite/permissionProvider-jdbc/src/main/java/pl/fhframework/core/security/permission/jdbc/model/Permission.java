package pl.fhframework.core.security.permission.jdbc.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.fhPersistence.core.BasePersistentObject;
import pl.fhframework.jpa.converter.LocalDateAttributeConverter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author tomasz.kozlowski (created on 2017-11-22)
 */
@Getter
@Setter
@Entity
@Table(name = "SEC_PERMISSIONS")
@SequenceGenerator(name = "SEQUENCE_GENERATOR", sequenceName = "SEC_PERMISSIONS_ID_SEQ")
public class Permission extends BasePersistentObject implements IPermission {

    @Column(name = "BUSINESS_ROLE_NAME", nullable = false)
    private String businessRoleName;

    @Column(name = "FUNCTION_NAME", nullable = false)
    private String functionName;

    @Column(name = "MODULE_UUID", nullable = false)
    private String moduleUUID;

    @Column(name = "CREATION_DATE")
    @Convert(converter = LocalDateAttributeConverter.class)
    private LocalDate creationDate;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "DENIAL")
    private Boolean denial;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        if (getId() == null || that.getId() == null) {
            return Objects.equals(businessRoleName, that.businessRoleName) &&
                   Objects.equals(functionName, that.functionName) &&
                   Objects.equals(moduleUUID, that.moduleUUID) &&
                   Objects.equals(denial, that.denial);
        }
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : Objects.hash(businessRoleName, functionName, moduleUUID, denial);
    }

}
