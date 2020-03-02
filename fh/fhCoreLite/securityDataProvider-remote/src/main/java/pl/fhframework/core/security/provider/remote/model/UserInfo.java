package pl.fhframework.core.security.provider.remote.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Tomasz Kozlowski (created on 20.05.2019)
 */
@Getter
@Setter
@JsonRootName("user")
public class UserInfo implements Serializable {

    @JsonProperty
    private String username;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String password;

    @JsonProperty
    private boolean blocked;

    @JsonProperty
    private boolean deleted;

    @JsonProperty
    private List<String> roles;

    @JsonProperty
    private Map<String, Object> attributes = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo)) return false;
        UserInfo that = (UserInfo) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

}
