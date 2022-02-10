package pl.fhframework.compiler.core.services.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.services.service.ServiceConsts;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "RestProperties", namespace = ServiceConsts.SERVICE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"resourceUri", "httpMethod", "restParameters"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RestProperties implements ISnapshotEnabled, Serializable, Cloneable {
    @XmlElement(name = "ResourceUri", required = true)
    private String resourceUri;

    @XmlElement(name = "HttpMethod", required = true)
    private RestMethodTypeEnum httpMethod;

    @XmlElements({
            @XmlElement(name = "RestParameter", type = RestParameter.class)
    })
    @XmlElementWrapper(name = "RestParameters", namespace = ServiceConsts.SERVICE_XSD)
    private List<RestParameter> restParameters = new ArrayList<>();
}
