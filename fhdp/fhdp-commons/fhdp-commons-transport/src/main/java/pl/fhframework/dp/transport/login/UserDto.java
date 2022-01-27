package pl.fhframework.dp.transport.login;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Setting;
import pl.fhframework.dp.commons.base.model.IPersistentLong;

import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-06-17
 */
@Getter @Setter
@Document(indexName = "#{@indexNamePrefix}_user")
@Setting(settingPath = "/settings/settings.json")
public class UserDto implements Serializable, IPersistentLong {

    @Id
    private Long id;
    private String username;
    private String password;
    private List<String> roles = new ArrayList<>();
    private String firstName;
    private String lastName;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean blocked = false;
    private boolean deleted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto that = (UserDto) o;
        if (id == null || that.id == null) {
            return Objects.equals(username, that.username);
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
