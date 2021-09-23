package pl.fhframework.core.security.permission.standalone.model;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.model.IPermission;

import javax.xml.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author Tomasz Kozlowski (created on 16.10.2019)
 */
@Getter
@Setter
@XmlRootElement(name = "Permission")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"businessRoleName", "functionName", "moduleUUID", "createDate", "createdBy", "denial"})
public class Permission implements IPermission {

    @XmlTransient
    private Long id;
    @XmlAttribute
    private String businessRoleName;
    @XmlAttribute
    private String functionName;
    @XmlAttribute
    private String moduleUUID;
    @XmlAttribute
    private String createDate;
    @XmlAttribute
    private String createdBy;
    @XmlAttribute
    private Boolean denial;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate getCreationDate() {
        return LocalDate.parse(createDate);
    }

    @Override
    public void setCreationDate(LocalDate creationDate) {
        createDate = creationDate.format(FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return Objects.equals(businessRoleName, that.businessRoleName) &&
                Objects.equals(functionName, that.functionName) &&
                Objects.equals(moduleUUID, that.moduleUUID) &&
                Objects.equals(denial, that.denial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessRoleName, functionName, moduleUUID, denial);
    }

}
