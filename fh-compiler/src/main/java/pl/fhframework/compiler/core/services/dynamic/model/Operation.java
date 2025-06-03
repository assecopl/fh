package pl.fhframework.compiler.core.services.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.services.service.ServiceConsts;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@Getter
@Setter
@XmlRootElement(name = "Operation", namespace = ServiceConsts.SERVICE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"restProperties", "rule"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Operation implements ISnapshotEnabled, Serializable, Cloneable {
    @XmlElementRef
    private Rule rule;

    @XmlElementRef
    private RestProperties restProperties;
}
