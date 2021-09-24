package pl.fhframework.core.security.permission.standalone.model;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tomasz Kozlowski (created on 16.10.2019)
 */
@Getter
@Setter
@XmlRootElement(name = "Registry")
@XmlAccessorType(XmlAccessType.FIELD)
public class PermissionRegistry {

    @XmlElements({
        @XmlElement(name = "Permission", type = Permission.class),
    })
    @XmlElementWrapper(name = "Permissions")
    private List<Permission> permissions = new LinkedList<>();

}
