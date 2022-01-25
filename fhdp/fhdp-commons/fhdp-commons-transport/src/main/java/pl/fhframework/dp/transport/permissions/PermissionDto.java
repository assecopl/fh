package pl.fhframework.dp.transport.permissions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.*;
import pl.fhframework.dp.commons.base.model.IPersistentLong;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 */
@Getter @Setter
@Document(indexName = "#{@indexNamePrefix}_permission")
@Setting(settingPath = "/settings/settings.json")
public class PermissionDto implements Serializable, IPersistentLong {
    
    @Id
    private Long id;
    private String businessRoleName;
    private String functionName;
    private String moduleUUID;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd")
    private LocalDate creationDate;
    private String createdBy;
    private Boolean denial;


    @Transient
    public boolean isDenial() {
        return (denial == null)?false:denial;
    }

    @Transient
    public void setDenial(boolean denial) {
        this.denial = denial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionDto that = (PermissionDto) o;
        if (id == null || that.id == null) {
            return Objects.equals(businessRoleName, that.businessRoleName) &&
                    Objects.equals(functionName, that.functionName) &&
                    Objects.equals(moduleUUID, that.moduleUUID) &&
                    Objects.equals(denial, that.denial);
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : Objects.hash(businessRoleName, functionName, moduleUUID, denial);
    }

}
