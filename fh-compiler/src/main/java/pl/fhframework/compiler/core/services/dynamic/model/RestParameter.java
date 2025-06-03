package pl.fhframework.compiler.core.services.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.services.service.ServiceConsts;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.core.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@XmlRootElement(name = "RestProperties", namespace = ServiceConsts.SERVICE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "ref", "type"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RestParameter implements ISnapshotEnabled, Serializable, Cloneable {
    @XmlAttribute
    private String name; // name of param within REST request

    @XmlAttribute
    private String ref; // reference to name of service param

    @XmlAttribute
    private RestParameterTypeEnum type;

    public String getResolvedName() {
        if (!StringUtils.isNullOrEmpty(name)) {
            return name;
        }

        return ref;
    }

    public void setResolvedName(String name) {
        if (!Objects.equals(this.name, this.ref)) {
            this.name = name;
        }
        else {
            this.name = null;
        }
    }
}
