package pl.fhframework.dp.transport.roles;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import pl.fhframework.dp.commons.base.model.IPersistentLong;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomasz Kozlowski (created on 26.01.2021)
 */
@Getter @Setter
@Document(indexName = "#{@indexNamePrefix}_role")
@Setting(settingPath = "/settings/settings.json")
public class RoleDto implements Serializable, IPersistentLong, Comparable<RoleDto> {

    @Id
    private Long id;
    private String roleName;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDto that = (RoleDto) o;
        if (id == null || that.id == null) {
            return Objects.equals(roleName, that.roleName);
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName);
    }

    @Override
    public int compareTo(RoleDto roleDto) {
        if (roleDto != null) {
            return this.roleName.compareToIgnoreCase(roleDto.roleName);
        }
        return 0;
    }

}
